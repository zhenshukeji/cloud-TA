<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable maxlength="16" show-word-limit @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item prop="isShelf">
        <el-select v-model="queryParams.isShelf" clearable placeholder="请选择课程状态">
          <el-option label="上架" value="1" />
          <el-option label="下架" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新建课程</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="courseList" stripe>
      <el-table-column label="课程名称" align="center" prop="courseName" />
      <el-table-column label="所属科目" align="center" prop="subject" />
      <el-table-column label="课程价格（元）" align="center" prop="amountCny">
        <template slot-scope="{row}">
          {{ row.amountCny }}元
        </template>
      </el-table-column>
      <el-table-column label="课次数量" align="center" prop="timeNum">
        <template slot-scope="{row}">
          {{ row.timeNum }}课次
        </template>
      </el-table-column>
      <el-table-column label="实收价格（元）" align="center" prop="paidAmountCny">
        <template slot-scope="{row}">
          {{ row.paidAmountCny }}元
        </template>
      </el-table-column>
      <el-table-column label="礼品赠送" align="center" prop="gift" />
      <el-table-column label="课程状态" align="center" prop="isShelf">
        <template slot-scope="{row}">
          {{ row.isShelf | statusFilter }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改课程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="780px" :close-on-click-modal="false">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-form-item label="封面图片:" prop="minCoverImage">
          <el-upload class="avatar-uploader" :action="action" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
            <img v-if="form.minCoverImage" :src="form.minCoverImage" style="width:90px;height:90px;display:block">
            <i v-else class="el-icon-plus avatar-uploader-icon" style="width:90px;height:90px;line-height:90px" />
          </el-upload>
          <div class="tip">
            支持png、jpg、jpeg、gif格式的图片，建议宽高180x180px;图片大小小于2M
          </div>
        </el-form-item>
        <el-form-item label="课程主图:" prop="coverImage">
          <el-upload class="avatar-uploader" :action="action" :show-file-list="false" :on-success="handleAvatarSuccess2" :before-upload="beforeAvatarUpload">
            <img v-if="form.coverImage" :src="form.coverImage" style="width:390px;height:150px;display:block">
            <i v-else class="el-icon-plus avatar-uploader-icon" style="width:390px;height:150px;line-height:150px" />
          </el-upload>
        </el-form-item>
        <el-form-item label="课程名称:" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" style="width:50%" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="课程简介：" prop="describe">
          <el-input v-model="form.describe" placeholder="请输入课程简介" type="textarea" :autosize="{ minRows: 3, maxRows: 3}" show-word-limit maxlength="50" style="width:50%" />
        </el-form-item>
        <el-form-item label="所属科目:" prop="subject">
          <el-input v-model="form.subject" placeholder="请输入所属科目" style="width:50%" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="课表颜色:" prop="color">
          <div class="color-box">
            <div v-for="(item,index) in colorArr" :key="index" class="color-item" :style="{borderColor:(index === parseInt(form.color) ? item :'#ffffff')}" @click="changeColor(index)">
              <div class="color-radius" :style="{backgroundColor:item}" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="课程状态" prop="isShelf">
          <el-select v-model="form.isShelf" placeholder="请选择课程类型">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程类型:" prop="isExperience">
          <el-select v-model="form.isExperience" placeholder="请选择课程类型" disabled>
            <el-option label="普通课程" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程价格:" prop="amountCny">
          <el-input v-model="form.amountCny" placeholder="请输入课程价格（元·线下收款登记）" style="width:50%" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="课次数量:" prop="timeNum">
          <el-input v-model="form.timeNum" placeholder="请输入课次数量" style="width:50%" maxlength="10" show-word-limit />
        </el-form-item>
        <el-form-item label="实收价格:" prop="paidAmountCny">
          <el-input v-model="form.paidAmountCny" placeholder="请输入实收价格（元·线下收款登记）" style="width:50%" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="礼品赠送:" prop="gift">
          <el-input v-model="form.gift" placeholder="请输入赠送礼品" style="width:50%" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="课程展示:" prop="introduction">
          <Editor v-model="form.introduction" @blur="changeIntroduction" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" style="margin-top:20px">
        <el-button type="primary" style="width:100px" @click="submitForm">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Editor from '@/components/Editor'
import {
  listCourse,
  getCourse,
  updateCourse,
  delCourse,
  addCourse
} from '@/api/educenter/course'

export default {
  name: 'Course',
  filters: {
    statusFilter(status) {
      const statusArr = ['下架', '上架']
      return statusArr[status]
    },
    ruleFilter(rule) {
      const ruleArr = ['不扣课次', '扣课次']
      return ruleArr[rule]
    }
  },
  components: {
    Editor
  },
  data() {
    var checkNumber = (rule, value, callback) => {
      var reg = /^[0-9]*[1-9][0-9]*$/
      if (!value && value !== 0) {
        return callback(new Error('不能为空'))
      } else {
        if (!reg.test(value)) {
          callback(new Error('请输入正整数'))
        } else {
          if (value < 0) {
            callback(new Error('输入的值必须大于0'))
          } else {
            callback()
          }
        }
      }
    }
    var checkPrice = (rule, value, callback) => {
      var reg = /^\d+(\.\d{1,2})?$/
      if (!value) {
        callback(new Error('价格不能为空'))
      } else if (value < 0) {
        callback(new Error('请输入大于零的数字'))
      } else if (!reg.test(value)) {
        callback(new Error('请输入有效的价格(大于零且最多保留两位小数)'))
      } else {
        callback()
      }
    }
    var checkContent = (rule, value, callback) => {
      console.log('触发了')
      if (!value) {
        return callback(new Error('内容不能为空'))
      } else if (value.length >= 30000) {
        return callback(
          new Error(
            '内容超出长度限制，请检查是否有未从编辑器里上传的图片、音频、视频。如果有，需要改成从编辑器里上传。'
          )
        )
      } else {
        callback()
      }
    }
    return {
      click: true,
      imageUrl: '',
      action: process.env.VUE_APP_BASE_API + '/common/upload',
      isAdd: true,
      colorArr: [
        '#387667',
        '#82BAD5',
        '#D1E9DC',
        '#89AC8B',
        '#E1BC8F',
        '#D97F4A',
        '#91583D',
        '#7B331D'
      ],
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 课程表格数据
      courseList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      edit: false,
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: undefined,
        isShelf: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        amountCny: [
          {
            required: true,
            validator: checkPrice,
            trigger: 'change'
          }
        ],
        paidAmountCny: [
          {
            required: true,
            validator: checkPrice,
            trigger: 'change'
          }
        ],
        courseName: [
          {
            required: true,
            message: '课程名称不能为空',
            trigger: 'blur'
          }
        ],
        describe: [
          {
            required: true,
            message: '课程简介不能为空',
            trigger: 'blur'
          }
        ],
        isExperience: [
          {
            required: true,
            message: '课程类型不能为空',
            trigger: 'blur'
          }
        ],
        color: [
          {
            required: true,
            message: '请选择课表颜色',
            trigger: 'blur'
          }
        ],
        introduction: [
          {
            required: true,
            validator: checkContent,
            trigger: ['blur', 'change']
          }
        ],
        isShelf: [
          {
            required: true,
            message: '请选择课程类型',
            trigger: ['blur', 'change']
          }
        ],
        minCoverImage: [
          {
            required: true,
            message: '请上传课程封面',
            trigger: ['blur', 'change']
          }
        ],
        coverImage: [
          {
            required: true,
            message: '请上传课程主图',
            trigger: ['blur', 'change']
          }
        ],
        timeNum: [
          {
            required: true,
            validator: checkNumber,
            trigger: 'change'
          }
        ],
        subject: [
          {
            required: true,
            message: '请输入所属科目',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        if (route.path === '/educenter/course') {
          this.queryParams.pageNum = 1
          this.queryParams.pageSize = 10
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /** 查询课程列表（社区版只有常规课程） */
    getList() {
      const query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      listCourse(query).then((response) => {
        this.courseList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 触发校验
    changeIntroduction() {
      this.$refs.form.validateField('introduction')
    },
    // 切换颜色
    changeColor(index) {
      this.form.color = index
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        amountCny: undefined,
        courseName: undefined,
        subject: undefined,
        color: 0,
        isExperience: 1,
        isShelf: undefined,
        timeNum: undefined,
        paidAmountCny: undefined,
        gift: undefined,
        introduction: undefined,
        minCoverImage: undefined,
        coverImage: undefined,
        describe: undefined
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.isAdd = true
      this.title = '新建课程'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.isAdd = false
      const id = row.id
      getCourse(id).then((response) => {
        this.form = response.data
        this.open = true
        this.title = '修改课程'
      })
    },

    /** 提交按钮 */
    submitForm: function() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.click) {
            this.click = false
            if (this.form.id !== undefined) {
              updateCourse(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('修改成功')
                    this.open = false
                    this.getList()
                  } else {
                    this.msgError(response.msg)
                  }
                })
                .finally(() => {
                  this.click = true
                })
            } else {
              addCourse(this.form)
                .then((response) => {
                  if (response.code === 200) {
                    this.msgSuccess('新增成功')
                    this.open = false
                    this.getList()
                  } else {
                    this.msgError(response.msg)
                  }
                })
                .finally(() => {
                  this.click = true
                })
            }
          }
        }
      })
    },
    handleAvatarSuccess(res, file) {
      if (res.code === 200) {
        console.log('进入')
        this.form.minCoverImage = res.url
        console.log(this.form.minCoverImage)
      }
    },
    handleAvatarSuccess2(res, file) {
      if (res.code === 200) {
        console.log('进入2')
        this.form.coverImage = res.url
        console.log(this.form.coverImage)
      }
    },
    beforeAvatarUpload(file) {
      const limitSize = file.size / 1024 / 1024 < 2
      var isPic =
        file.type === 'image/jpeg' ||
        file.type === 'image/png' ||
        file.type === 'image/jpg' ||
        file.type === 'image/gif'
      if (!isPic) {
        this.$message({
          message: '请上传jpg/jpeg/png/gif类型的图片文件',
          type: 'error',
          duration: 2000
        })
      }
      if (!limitSize) {
        this.$message({
          message: '上传的图片大小不能超过2M',
          type: 'error',
          duration: 2000
        })
      }
      return isPic && limitSize
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const h = this.$createElement
      this.$confirm(
        h('p', null, [
          h('span', null, `是否确认删除课程名为"${row.courseName}"的课程?`),
          h(
            'p',
            { style: 'font-size: 12px;color: red' },
            '注：已有班级绑定的课程不可删除'
          )
        ]),
        '警告',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
        .then(function() {
          return delCourse(row.id)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
        .catch(function() {})
    }
  }
}
</script>
<style lang="scss" scoped>
.avatar-uploader-icon {
  width: 130px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}
.avatar {
  width: 130px;
  height: 100px;
}
.color-box {
  display: flex;
  margin-top: 4px;
  .color-item {
    border-radius: 50%;
    width: 28px;
    height: 28px;
    cursor: pointer;
    border-width: 2px;
    border-style: solid;
    position: relative;
    .color-radius {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 14px;
      height: 14px;
      border-radius: 50%;
    }
  }
}
</style>
