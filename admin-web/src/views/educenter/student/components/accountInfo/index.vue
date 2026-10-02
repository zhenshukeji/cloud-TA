<template>
  <div>
    <!-- 账户信息 -->
    <el-dialog title="学员账户信息" :visible.sync="infoVisible" width="50%" class="trainee-account" :before-close="handleClose" :close-on-click-modal="false">
      <el-divider />
      <!-- 社区版不提供学员资金账户能力，此处不再展示账户余额信息 -->
      <div class="pay-kc">
        <el-button type="primary" @click="addBuyInfo">+课程购买记录</el-button>
      </div>
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="课次信息" name="lessonsInfo">
          <el-table v-loading="loading" :data="lessonsList" stripe>
            <el-table-column label="课程名称" align="center" prop="courseName" />
            <el-table-column label="班级名称" align="center" prop="className" />
            <el-table-column label="总课次" align="center" prop="totalNumber" />
            <el-table-column label="剩余课次" align="center" prop="remainingNumber" />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
              <template slot-scope="{row}">
                <el-button size="mini" type="text" @click="handleClassHour(row,0)">增加</el-button>
                <el-button size="mini" type="text" @click="handleClassHour(row,1)">减去</el-button>
                <el-button size="mini" type="text" @click="handleTotalHour(row)">修改总课次</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="修改记录" name="editRecord">
          <el-table v-loading="loading" :data="xgList" stripe>
            <el-table-column label="课程名称" align="center" prop="courseName" />
            <el-table-column label="操作" align="center" prop="operation" />
            <el-table-column label="操作原因" align="center" prop="reason" />
            <el-table-column label="操作人" align="center" prop="operationName" />
          </el-table>

        </el-tab-pane>
        <el-tab-pane label="课程购买记录" name="buyRecord">
          <el-table v-loading="loading" :data="gmList" stripe>
            <el-table-column label="课程名称" align="center" prop="courseName" />
            <el-table-column label="课次数量" align="center" prop="timeNum">
              <template slot-scope="{row}">
                {{ row.timeNum }}课次
              </template>
            </el-table-column>
            <el-table-column label="购买金额（元）" align="center" prop="amountCny">
              <template slot-scope="{row}">
                {{ row.amountCny }}元
              </template>
            </el-table-column>
            <el-table-column label="操作人" align="center" prop="operationName" />
            <el-table-column label="备注" align="center" prop="remark" />
          </el-table>
        </el-tab-pane>
        <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList(tableIndex)" />
      </el-tabs>
    </el-dialog>
    <operate-classTime :showLesson="showLesson" :classData="classData" :isAdd="isAdd" @close="showLesson = false" @operateClassHour="operateClassHour" />

    <!-- 新增课程购买记录 -->
    <el-dialog title="新增课程购买记录" :visible.sync="addPayPopup" width="30%" class="add-class-popup">
      <el-divider />
      <el-form ref="lessonsData" :model="ksjlData" :rules="rules" label-width="100px" label-position="left">
        <el-form-item label="课程名称:" prop="kcValue">
          <el-select v-model="ksjlData.kcValue" placeholder="请选择" @change="changeLesson" ref="selection">
            <el-option v-for="(item,index) in kcOptions" :key="index" :label="item.label" :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="课次数量:" prop="timeNum">
          <el-input v-model="ksjlData.timeNum" type="number" placeholder="请输入课次数量" maxlength="10" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="购买金额:" prop="amountCny">
          <el-input v-model="ksjlData.amountCny" type="number" placeholder="请输入购买金额（元·线下收款登记）"></el-input>
        </el-form-item>
        <el-form-item label="备注:" prop="remark">
          <el-input v-model="ksjlData.remark" placeholder="请输入备注" maxlength="40" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addBuyRecord" style="width:100px">保存</el-button>
      </div>
    </el-dialog>

    <!-- 修改总课次 -->
    <el-dialog title="修改总课次" :visible.sync="totalVisible" width="500px">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" label-position="left">
        <el-form-item label="课程名称:">
          {{ form.courseName }}
        </el-form-item>
        <el-form-item label="总课次:">
          {{ form.totalNumber }}
        </el-form-item>
        <el-form-item label="剩余课次:">
          {{ form.remainingNumber }}
        </el-form-item>
        <el-form-item label="总课次:" prop="num">
          <el-input v-model="form.num" placeholder="请输入总课次" maxlength="10" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="修改原因:" prop="reason">
          <el-input v-model="form.reason" placeholder="请输入修改原因" maxlength="40" show-word-limit></el-input>
        </el-form-item>
        <div class="flex">
          <span class="warn">
            <svg-icon icon-class="warn" /> 课次为敏感数据，请谨慎操作！
          </span>
          <el-button type="danger" style="width:100px" @click="opeTotalHour">
            保存
          </el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {
  getInfoWithClassTime,
  getInfoWithClsTimeUp,
  getInfoWithCouPur,
  updateCourseTime,
  courseSelectAll,
  purchase,
  opeTotalHour
} from '@/api/educenter/student'
import operateClassTime from '../operateClassTime'
export default {
  components: {
    operateClassTime
  },
  props: {
    infoVisible: {
      type: Boolean,
      default: false
    },
    infoData: {
      type: Object,
      default: () => ({})
    }
  },
  watch: {
    infoVisible(val) {
      if (val) {
        this.queryParams.id = this.infoData.id
        this.queryParams.pageNum = 1
        this.queryParams.pageSize = 5
        this.activeName = 'lessonsInfo'
        this.tableIndex = 0
        this.kcOptions = []
        this.getList(0)
      }
    }
  },
  data() {
    var checkClassHour = (rule, value, callback) => {
      var reg = /^[0-9]*[1-9][0-9]*$/
      if (!value && value != 0) {
        return callback(new Error('课次不能为空'))
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
    return {
      buyClick: true,
      form: {},
      totalVisible: false,
      lessonId: '',
      kcOptions: [],
      addPayPopup: false,
      showLesson: false,
      classData: {},
      isAdd: false,
      total: 0,
      activeName: 'lessonsInfo',
      loading: false,
      lessonsList: [],
      xgList: [],
      gmList: [],
      ksjlData: {
        kcValue: '',
        timeNum: '',
        amountCny: '',
        remark: ''
      },
      queryParams: {
        pageNum: 1,
        pageSize: 5,
        id: undefined,
        studentName: undefined,
        studentsPhone: undefined
      },
      rules: {
        kcValue: [
          {
            required: true,
            message: '请选择课程名称',
            trigger: 'change'
          }
        ],
        timeNum: [
          {
            required: true,
            validator: checkClassHour,
            trigger: ['change', 'blur']
          }
        ],
        amountCny: [
          {
            required: true,
            validator: checkClassHour,
            trigger: ['change', 'blur']
          }
        ],
        num: [
          {
            required: true,
            validator: checkClassHour,
            trigger: ['change', 'blur']
          }
        ],
        reason: [
          {
            required: true,
            message: '请输入修改原因',
            trigger: ['change', 'blur']
          }
        ]
      }
    }
  },
  mounted() {},
  methods: {
    getList(index) {
      for (var key in this.queryParams) {
        if (
          this.queryParams[key] === '' ||
          this.queryParams[key] === undefined
        ) {
          delete this.queryParams[key]
        }
      }
      this.loading = true
      if (index === 0) {
        getInfoWithClassTime(this.queryParams).then((res) => {
          this.ksData = res
          this.lessonsList = res.rows
          this.loading = false
          this.total = res.total
        })
      } else if (index === 1) {
        getInfoWithClsTimeUp(this.queryParams).then((res) => {
          this.xgData = res
          this.xgList = res.rows
          this.loading = false
          this.total = res.total
        })
      } else {
        getInfoWithCouPur(this.queryParams).then((res) => {
          this.gmData = res
          this.gmList = res.rows
          this.loading = false
          this.total = res.total
        })
      }
    },
    // 打开增减课次
    handleClassHour(data, index) {
      if (index === 0) {
        this.isAdd = true
      } else {
        this.isAdd = false
      }
      this.classData.courseName = data.courseName
      this.classData.remainingNumber = data.remainingNumber
      this.classData.id = data.id
      this.showLesson = true
    },
    // 重置
    reset() {
      this.form = {
        num: undefined,
        reason: undefined
      }
      this.resetForm('form')
    },
    // 修改总课次
    handleTotalHour(row) {
      this.reset()
      this.totalVisible = true
      this.form = { ...this.form, ...row }
    },
    // 确认课次
    opeTotalHour() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          opeTotalHour({
            courseTimeId: this.form.id,
            num: this.form.num,
            reason: this.form.reason
          }).then((res) => {
            if (res.code === 200) {
              this.getList(this.tableIndex)
              this.msgSuccess('修改成功')
              this.totalVisible = false
            }
          })
        }
      })
    },
    // 新增课程购买记录
    addBuyInfo() {
      this.addPayPopup = true
      this.courseSelectAll()
      // 清空校验字段
      this.$nextTick(() => {
        this.$refs['lessonsData'].resetFields()
      })
    },
    // 修改课程
    addBuyRecord() {
      this.$refs['lessonsData'].validate((valid) => {
        if (valid) {
          this.purchase({
            courseId: this.lessonId,
            studentId: this.infoData.id,
            timeNum: this.ksjlData.timeNum,
            amountCny: this.ksjlData.amountCny,
            remark: this.ksjlData.remark
          })
        }
      })
    },
    // 购买课次
    purchase(data) {
      if (this.buyClick) {
        this.buyClick = false
        purchase(data)
          .then((res) => {
            if (res.code === 200) {
              this.addPayPopup = false
              // 更新数据
              this.handleClick()
              this.msgSuccess('操作成功')
            }
          })
          .finally(() => {
            this.buyClick = true
          })
      }
    },
    // 获取所有课程
    courseSelectAll() {
      this.kcOptions = []
      courseSelectAll().then((res) => {
        if (res.code === 200) {
          // this.coursList = res.rows;
          res.rows.map((arr, index) => {
            this.kcOptions.push({
              id: arr.id,
              label: arr.courseName
            })
          })
        } else {
          this.msgError(res.msg)
        }
      })
    },
    // 切换tab菜单
    handleClick() {
      this.queryParams.pageNum = 1
      this.queryParams.pageSize = 10
      switch (this.activeName) {
        case 'lessonsInfo':
          this.tableIndex = 0
          break
        case 'editRecord':
          this.tableIndex = 1
          break
        case 'buyRecord':
          this.tableIndex = 2
          break
      }
      this.getList(this.tableIndex)
    },
    //账户信息
    handleAccount(row) {
      this.queryParams.pageSize = 10
      this.queryParams.pageNum = 1
      this.queryParams.id = row.id
      this.id = row.id
      this.getList(0)
      this.activeName = 'lessonsInfo'
      this.infoVisible = true
    },
    // 增减课次
    operateClassHour(data) {
      updateCourseTime(data).then((res) => {
        if (res.code === 200) {
          this.showLesson = false
          this.msgSuccess('修改课次成功')
          this.getList(this.tableIndex)
        }
      })
    },
    // 新增课程购买记录时选中新增的课程
    changeLesson(res) {
      this.lessonId = res
    },
    // 关闭关联弹窗
    handleClose() {
      this.$emit('close')
    }
  }
}
</script>

<style lang='scss' scoped>
.trainee-details {
  .el-form-item {
    margin-bottom: 0;
  }
  img {
    width: 60px;
    height: 60px;
  }
}
.trainee-account {
  .money {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30px;
  }
}
.add-class-popup {
  .addClass-form {
    display: flex;
    flex-direction: column;
    .el-form-item {
      margin-bottom: 20px;
    }
  }
  .btn-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
.pay-kc {
  position: absolute;
  top: 75px;
  right: 20px;
  z-index: 5;
}
.flex {
  display: flex;
  justify-content: space-around;
  align-items: center;
  .warn {
    color: #ff6206;
  }
}
::v-deep .el-pagination {
  text-align: center;
}
</style>
