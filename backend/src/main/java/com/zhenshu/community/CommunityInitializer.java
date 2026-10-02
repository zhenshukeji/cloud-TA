package com.zhenshu.community;

import com.zhenshu.common.utils.SecurityUtils;
import com.zhenshu.common.utils.StringUtils;
import com.zhenshu.jianxue.mec.project.system.domain.SysUser;
import com.zhenshu.jianxue.mec.project.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 云朵教务社区版 · 一次性初始化流程（OS009 / 功能边界 C01）。
 *
 * <p>社区版不提供任何内置口令。数据库种子（{@code database/002_seed_system.sql}）建好唯一的
 * 机构、校区与管理员账号，但 {@code sys_user.password} 留空；本流程在启动时读取环境变量
 * {@code INITIAL_ADMIN_PASSWORD}，写入 BCrypt 哈希，使管理员可以完成首次登录。
 *
 * <p>判定规则（三条，互斥）：
 * <ol>
 *   <li><b>已完成初始化</b>（管理员口令非空）：跳过。因此重启、容器滚动更新都不再需要该环境变量，
 *       也不会覆盖运维自行修改过的口令；</li>
 *   <li><b>待初始化且已设置变量</b>：写入 BCrypt 哈希，并在写后复核；</li>
 *   <li><b>待初始化且未设置变量</b>：记录明确的失败日志并抛出异常，让进程以非 0 退出。
 *       这里刻意不做任何「默认口令」兜底 —— 一个能被猜到的默认口令等价于没有密码。</li>
 * </ol>
 *
 * <p>OS011 起，口令强度由「只告警」升级为**硬校验**：长度不足、字符种类不足、
 * 等于登录账号或命中常见弱口令清单都会直接中止启动（社区版不提供任何默认口令，
 * 唯一的初始化入口就是这个环境变量，弱口令等价于没有口令）。
 * 「令牌密钥强度」与「必需变量非空」由 {@link CommunityStartupGuard} 负责。
 *
 * <p>安全约定：口令只在内存中流转，不写日志、不写配置文件；日志里只出现来源变量名与口令长度。
 * 管理员首次登录后应立即通过「个人信息 - 修改密码」更换口令。
 *
 * @author OS009
 */
@Component
public class CommunityInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CommunityInitializer.class);

    /** 种子约定的管理员登录账号，见 database/002_seed_system.sql */
    private static final String ADMIN_USER_NAME = "admin";

    /** 初始口令来源：环境变量（也支持 {@code --INITIAL_ADMIN_PASSWORD=...} 启动参数） */
    private static final String PASSWORD_PROPERTY = "INITIAL_ADMIN_PASSWORD";

    /** 口令长度下限：低于此值直接拒绝启动（OS011 起由「只告警」改为「硬失败」） */
    private static final int MIN_PASSWORD_LENGTH = 10;

    /** 至少需要的字符种类数（小写 / 大写 / 数字 / 符号） */
    private static final int MIN_PASSWORD_CHAR_CLASSES = 2;

    /** 一眼假的示例口令（小写比较），命中即拒绝 */
    private static final String[] PASSWORD_BLACKLIST = {
            "admin", "admin123", "admin@123", "administrator", "password", "passw0rd",
            "123456", "12345678", "123456789", "1234567890", "changeme", "change-me",
            "placeholder", "local-placeholder", "test", "demo", "root", "qwerty"
    };

    @Resource
    private ISysUserService userService;

    /** 缺省为空串：既要能区分「未设置」，又不能让占位符解析失败导致启动报无关错误 */
    @Value("${" + PASSWORD_PROPERTY + ":}")
    private String initialAdminPassword;

    @Override
    public void run(ApplicationArguments args) {
        SysUser admin = userService.selectUserByUserName(ADMIN_USER_NAME);
        if (admin == null) {
            log.error("[社区版初始化] 未找到管理员账号 {}。请先按顺序执行 database/001_schema.sql 与 "
                    + "database/002_seed_system.sql 初始化单机构单校区数据。", ADMIN_USER_NAME);
            throw new IllegalStateException("社区版启动中止：缺少种子数据，未找到管理员账号 "
                    + ADMIN_USER_NAME + "（请确认已执行 001_schema.sql 与 002_seed_system.sql）");
        }

        if (StringUtils.isNotEmpty(admin.getPassword())) {
            log.info("[社区版初始化] 管理员账号 {} 已完成初始化，跳过口令写入；"
                    + "本次启动不读取环境变量 {}。", ADMIN_USER_NAME, PASSWORD_PROPERTY);
            return;
        }

        String password = initialAdminPassword == null ? "" : initialAdminPassword.trim();
        if (password.isEmpty()) {
            log.error("**********************************************************************");
            log.error("社区版启动中止：未设置必需环境变量 {}。", PASSWORD_PROPERTY);
            log.error("数据库中的管理员账号 {} 尚无口令，而社区版不提供任何内置默认口令。", ADMIN_USER_NAME);
            log.error("请在首次启动前设置该变量，例如：");
            log.error("    export {}=<你的初始口令>      # Linux / macOS", PASSWORD_PROPERTY);
            log.error("    set {}=<你的初始口令>         # Windows CMD", PASSWORD_PROPERTY);
            log.error("**********************************************************************");
            throw new IllegalStateException("社区版启动中止：未设置必需环境变量 " + PASSWORD_PROPERTY
                    + "，且管理员账号 " + ADMIN_USER_NAME + " 的口令仍为空；拒绝使用任何内置默认口令启动。");
        }

        String reason = checkPasswordStrength(password);
        if (reason != null) {
            log.error("**********************************************************************");
            log.error("社区版启动中止：{} 的强度不足（{}）。", PASSWORD_PROPERTY, reason);
            log.error("要求：长度 ≥ {} 位，且至少包含小写/大写/数字/符号中的 {} 类，"
                            + "且不等于登录账号 {}、不在常见弱口令清单内。",
                    MIN_PASSWORD_LENGTH, MIN_PASSWORD_CHAR_CLASSES, ADMIN_USER_NAME);
            log.error("请更换为随机生成的高熵口令（例如 `openssl rand -base64 18`），该变量只在首次启动生效。");
            log.error("**********************************************************************");
            throw new IllegalStateException("社区版启动中止：" + PASSWORD_PROPERTY + " 强度不足（" + reason + "）");
        }

        int rows = userService.resetUserPwd(ADMIN_USER_NAME, SecurityUtils.encryptPassword(password));
        SysUser after = userService.selectUserByUserName(ADMIN_USER_NAME);
        if (rows != 1 || after == null || StringUtils.isEmpty(after.getPassword())) {
            log.error("[社区版初始化] 写入管理员 {} 的初始口令失败：更新行数={}，回读口令是否为空={}。",
                    ADMIN_USER_NAME, rows, after == null || StringUtils.isEmpty(after.getPassword()));
            throw new IllegalStateException("社区版启动中止：管理员初始口令写入失败");
        }
        log.info("[社区版初始化] 已为管理员账号 {} 写入初始口令的 BCrypt 哈希（来源 {}，口令长度 {}，"
                + "口令值不落日志）；请首次登录后立即修改。", ADMIN_USER_NAME, PASSWORD_PROPERTY, password.length());
    }

    /**
     * 初始口令强度校验（OS011）。
     *
     * @return null 表示通过；否则返回「不通过的原因」（不返回口令内容）
     */
    private String checkPasswordStrength(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "长度 " + password.length() + " < " + MIN_PASSWORD_LENGTH;
        }
        if (password.equalsIgnoreCase(ADMIN_USER_NAME) || password.toLowerCase().contains(ADMIN_USER_NAME)) {
            return "与登录账号相同或包含登录账号";
        }
        String lower = password.toLowerCase();
        for (String weak : PASSWORD_BLACKLIST) {
            if (lower.equals(weak)) {
                return "命中常见弱口令清单";
            }
        }
        int classes = 0;
        if (password.chars().anyMatch(Character::isLowerCase)) {
            classes++;
        }
        if (password.chars().anyMatch(Character::isUpperCase)) {
            classes++;
        }
        if (password.chars().anyMatch(Character::isDigit)) {
            classes++;
        }
        if (password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) {
            classes++;
        }
        if (classes < MIN_PASSWORD_CHAR_CLASSES) {
            return "字符种类只有 " + classes + " 类，要求 ≥ " + MIN_PASSWORD_CHAR_CLASSES + " 类";
        }
        return null;
    }
}
