<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="left" class="form">
      <el-row>
        <el-col :lg="12" :md="24">
          <el-form-item label="机构名称：" prop="name">
            <el-input v-model="form.name" placeholder="请输入机构名称" style="width:300px" maxlength="16" show-word-limit/>
            <el-button v-if="form.status === 2" type="primary" class="save_btn" @click="updateInfo(0)">保存</el-button>
          </el-form-item>
        </el-col>
        <el-col :lg="12" :md="24">
          <el-form-item label="负责人名称：" prop="chargeName">
            <el-input v-model="form.chargeName" placeholder="请输入负责人名称" style="width:300px" maxlength="16" show-word-limit/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :lg="12" :md="24">
          <el-form-item label="机构logo：" prop="logo">
            <div class="logo_box">
              <el-upload :action="action" :show-file-list="false" :before-upload="(file) => handleBefore(file,2)" :on-success="(res,file) => handleSuccess(res,file,0)" accept='.jpg,.jpeg,.png,.gif'>
                <img v-if="form.logo" :src="form.logo" style="width:104px;height:104px" class="up-img">
                <div v-else class="upload" style="width:104px;height:104px">
                  <span class="upload-icon">+</span>
                  <span class="upload-tip">
                    上传机构logo
                  </span>
                </div>
              </el-upload>
              <el-button v-if="form.status === 2" type="primary" class="save_btn" @click="updateInfo(1)">保存</el-button>
            </div>
            <p class="tip">支持png、jpg、jpeg、gif格式的图片，建议宽高98x98px;图片大小小于2M</p>
          </el-form-item>
        </el-col>
        <el-col :lg="12" :md="24">
          <el-form-item label="负责人手机号：" prop="phoneNum">
            {{ form.phoneNum }}
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :lg="12" :md="24">
          <el-form-item label="封面图片：" prop="mainImg">
            <el-upload :action="action" :show-file-list="false" :before-upload="(file) => handleBefore(file,2)" :on-success="(res,file) => handleSuccess(res,file,3)" accept='.jpg,.jpeg,.png,.gif'>
              <img v-if="form.mainImg" :src="form.mainImg" style="width:400px;height:120px" class="up-img">
              <div v-else class="upload" style="width:400px;height:120px">
                <span class="upload-icon">+</span>
                <span class="upload-tip">
                  上传封面图片
                </span>
              </div>
            </el-upload>
            <p class="tip">支持png、jpg、jpeg、gif格式的图片，建议宽高400x120px;图片大小小于2M</p>
          </el-form-item>
        </el-col>
        <el-col :lg="12" :md="24">
          <el-form-item label="营业执照：" prop="licenseCode">
            <el-input v-model="form.licenseCode" placeholder="请输入营业执照编号" style="width:300px" maxlength="30" show-word-limit/>
          </el-form-item>
          <el-form-item prop="license">
            <el-upload :action="action" :show-file-list="false" :before-upload="(file) => handleBefore(file,2)" :on-success="(res,file) => handleSuccess(res,file,2)" accept='.jpg,.jpeg,.png,.gif' style="width:180px;height:100px">
              <img v-if="form.license" :src="form.license" style="width:180px;height:100px" class="up-img">
              <div v-else class="upload" style="width:180px;height:100px">
                <span class="upload-icon">+</span>
                <span class="upload-tip">
                  上传机构营业执照
                </span>
              </div>
            </el-upload>
            <p class="tip">编号和照片二者选一即可</p>
            <p class="tip">支持png、jpg、jpeg、gif格式的图片，建议宽高180x100px;图片大小小于2M</p>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :lg="12" :md="24">
          <el-form-item label="办学许可证：" prop="permitCode">
            <el-input v-model="form.permitCode" placeholder="请输入办学许可证编号" style="width:300px" maxlength="30" show-word-limit/>
          </el-form-item>
          <el-form-item prop="permit">
            <el-upload :action="action" :show-file-list="false" :before-upload="(file) => handleBefore(file,2)" :on-success="(res,file) => handleSuccess(res,file,1)" accept='.jpg,.jpeg,.png,.gif'>
              <div v-if="form.permit" class="premit_img">
                <img :src="form.permit" style="width:180px;height:100px" class="up-img">
                <i class="el-icon-delete delete" @click.stop="delPermit"></i>
              </div>
              <div v-else class="upload" style="width:180px;height:100px">
                <span class="upload-icon">+</span>
                <span class="upload-tip">
                  上传办学许可证
                </span>
              </div>
            </el-upload>
            <p class="tip">编号和照片二者选一即可</p>
            <p class="tip">支持png、jpg、jpeg、gif格式的图片，建议宽高180x100px;图片大小小于2M</p>
          </el-form-item>
        </el-col>
        <el-col :lg="12" :md="24">
          <div class="check_box">
            <div>
              <span style="fontWeight:bold">审核状态：</span>
              <span v-if="form.status === 0" style="color:#4F93FE">待提交</span>
              <span v-if="form.status === 1" style="color:#FE942D">审核中</span>
              <span v-if="form.status === 2" style="color:#35A27C">审核通过</span>
              <span v-if="form.status === 3" style="color:#FD1A1A">审核未通过</span>
            </div>
            <div v-if="form.status === 3" style="margin-top:20px">拒绝原因：{{ form.refuseReason }}</div>
            <div v-if="form.status === 0" class="tip">
              注：审核通过后仅能修改部分字段,请谨慎填写(审核通过后方可使用其他功能)
            </div>
          </div>
        </el-col>
      </el-row>
    </el-form>
    <div class="dialog-footer">
      <el-button type="primary" @click="submitForm">
        {{ form.status | btnFilter }}
      </el-button>
      <span v-if="form.status === 2" class="check_tip">
        注：审核通过后只能修改机构logo和机构名称，其他修改后需要重新审核，审核期间功能将无法使用，请慎重修改。
      </span>
    </div>

    <!-- 社区版（OS011）：换绑机构负责人手机号原依赖短信验证码，已随短信链路移除；
         需要修改负责人手机号请到「系统管理 - 用户管理」编辑对应账号。 -->

  </div>
</template>

<script>
import {
  getMechanismInfo,
  updateMechanismInfo,
  updateInfo
} from '@/api/mechanism/info'
import { tel } from '@/utils/validate.js'
export default {
  name: 'Mechanism',
  filters: {
    statusFilter(status) {
      var statusArr = ['未提交', '审核未通过', '审核通过']
      return statusArr[status]
    },
    btnFilter(type) {
      var btnArr = ['提交审核', '修改审核信息', '重新审核', '重新提交']
      return btnArr[type]
    }
  },
  data() {
    // var checkpermit = (rule, value, callback) => {
    //   if (!this.form.permitCode && !this.form.permit) {
    //     callback(new Error('编号和照片二者至少输入一个'))
    //   } else {
    //     callback()
    //   }
    // }
    var checklicense = (rule, value, callback) => {
      if (!this.form.licenseCode && !this.form.license) {
        callback(new Error('编号和照片二者至少输入一个'))
      } else {
        callback()
      }
    }
    return {
      //上传图片的请求地址拼接
      action: process.env.VUE_APP_BASE_API + '/common/upload',
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 总条数
      total: 0,
      // 机构信息表格数据
      mechanismList: [],
      // 弹出层标题
      title: '',
      // 是否显示新建还是修改弹出层
      edit: false,
      open: false,
      // 表单参数
      form: {},
      // 修改机构信息的验证
      rules: {
        chargeName: [
          { required: true, message: '负责人名称不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '机构名称不能为空', trigger: 'blur' }
        ],
        mainImg: [
          { required: true, message: '请上传封面图片', trigger: 'change' }
        ],
        phoneNum: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' }
        ],
        logo: [{ required: true, message: '请上传机构logo', trigger: 'blur' }],
        // permitCode: [{ validator: checkpermit, trigger: 'blur' }],
        // permit: [{ validator: checkpermit, trigger: 'blur' }],
        regionId: [
          { required: true, message: '所在城市不能为空', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '详细地址不能为空', trigger: 'blur' }
        ],
        licenseCode: [
          { required: true, validator: checklicense, trigger: 'blur' }
        ],
        license: [{ validator: checklicense, trigger: 'blur' }]
      },
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询机构信息列表 */
    getList() {
      this.loading = true
      getMechanismInfo().then((response) => {
        this.form = response.data
        this.loading = false
        console.log('机构信息列表', this.mechanismList)
      })
    },
    reset() {
      this.form = {
        name: undefined,
        mainImg: undefined,
        logo: undefined,
        permitCode: undefined,
        permit: undefined,
        chargeName: undefined,
        phoneNum: undefined,
        licenseCode: undefined,
        license: undefined,
        inviteCode: undefined
      }
    },
    // 限制上传文件的大小和类型
    handleBefore(file, size) {
      const isPic = [
        'image/jpeg',
        'image/png',
        'image/jpg',
        'image/gif'
      ].includes(file.type)
      const isLt4M = file.size / 1024 / 1024 < size

      if (!isPic) {
        this.$message.error('请上传规定的图片格式')
      }
      if (!isLt4M) {
        this.$message.error(`上传图片大小不能超过 ${size}MB!`)
      }
      return isPic && isLt4M
    },
    // 上传成功的回调
    handleSuccess(res, file, type) {
      console.log(res)
      if (res.code == '200') {
        if (type === 0) {
          this.form.logo = res.url
        } else if (type === 1) {
          this.form.permit = res.url
        } else if (type === 2) {
          this.form.license = res.url
        } else {
          this.form.mainImg = res.url
        }
        console.log('上传图片成功')
      } else {
        console.log('上传图片失败')
      }
    },
    // 更新logo和名字
    updateInfo(type) {
      const { id, logo, name } = this.form
      let data = {
        id
      }
      if (type === 0) {
        if (!this.form.name) {
          this.msgError('机构名字不能为空')
          return
        }
        data.name = name
      }
      if (type === 1) {
        if (!this.form.mainImg) {
          this.msgError('封面图片不能为空')
          return
        }
        data.logo = logo
      }
      updateInfo(data).then((res) => {
        if (res.code === 200) {
          this.msgSuccess('保存成功')
          this.getList()
        }
      })
    },
    // 删除办学许可证
    delPermit() {
      this.form.permit = ''
    },
    /** 提交修改按钮 */
    submitForm: function () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          updateMechanismInfo(this.form).then((response) => {
            if (response.code === 200) {
              this.getList()
              this.msgSuccess('修改成功')
              // 待提交不用刷新页面
              if (this.form.status !== 0) {
                location.reload()
              }
            } else {
              this.msgError(response.msg)
            }
          })
        }
      })
    },
  }
}
</script>
<style lang="scss" scoped>
.app-container {
  width: 1300px;
}
.flex {
  display: flex;
}
.form {
  .save_btn {
    margin-left: 20px;
  }
  .logo_box {
    display: flex;
    align-items: center;
  }
}
.dialog-footer {
  display: flex;
  justify-content: center;
  // width: 1200px;
}
.check_box {
  width: 500px;
  margin-top: 30px;
  padding: 20px;
  border: 5px solid #4f93fe;
  font-size: 16px;
  .tip {
    margin-top: 10px;
    color: #101010;
    font-size: 14px;
    color: red;
  }
}
.tip {
  font-size: 12px;
  color: #bbbbbb;
  line-height: 12px;
  padding: 0;
}
.check_tip {
  color: #732b00;
  font-size: 16px;
  margin-left: 20px;
  line-height: 36px;
  height: 36px;
}
.upload {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: #dddddd;
  border: 1px dashed #000000;
  border-radius: 6px;
  cursor: pointer;
  .upload-icon {
    font-size: 36px;
  }
  .upload-tip {
    font-size: 12px;
  }
}
.change_btn {
  background: #fe942d;
  border: none;
  color: #ffffff;
  margin-left: 20px;
  box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.4);
}
.upload:hover {
  border-color: #409eff;
}
.premit_img {
  position: relative;
  .up-img {
    border: 1px dashed #666666;
    border-radius: 6px;
  }
  .delete {
    position: absolute;
    top: 5px;
    right: 5px;
    color: #409eff;
  }
  .delete:hover {
    color: red;
  }
}

.remove {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 100;
}
</style>