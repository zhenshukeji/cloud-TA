<template>
  <div>
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item prop="courseName">
        <el-input v-model="queryParams.courseName" placeholder="请输入课程名称" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item prop="className">
        <el-input v-model="queryParams.className" placeholder="请输入班级名称" clearable @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="recordList" stripe>
      <el-table-column label="上课课程" align="center" prop="courseName" />
      <el-table-column label="上课时间" align="center" prop="classStartTime">
        <template slot-scope="{row}">
          <span>
            {{ parseTime(row.classStartTime, '{y}-{m}-{d}') + '('+parseTime(row.classStartTime, '{h}:{i}') +'-'+parseTime(row.classEndTime, '{h}:{i}')+")" }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="上课班级" align="center" prop="className" />
      <el-table-column label="主教老师" align="center" prop="bishopTeacherName" />
      <el-table-column label="辅助老师" align="center" prop="helpTeacherName" />
      <el-table-column label="实到人数" align="center" prop="realNumber">
        <template slot-scope="{row}">
          {{ row.realNumber }} / {{ row.shouldNumber }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button type="text" @click="onCllRecord(row.id)">点名记录</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 点名记录 -->
    <el-dialog title="点名记录" :visible.sync="callRecord" width="55%">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="100px" label-position="left" class="record">
        <el-form-item label="上课班级:">
          {{ form.className }}
        </el-form-item>
        <el-form-item label="上课时间:">
          <span>{{ parseTime(form.classStartTime, '{m}-{d} {a} {h}:{i}')+'-'+parseTime(form.classEndTime, '{h}:{i}') }}</span>
        </el-form-item>
        <el-form-item label="主教老师：" prop="classStartTime">
          {{ form.bishopTeacherName }}
        </el-form-item>
        <el-form-item label="辅助老师：" prop="leaveReason">
          {{ form.helpTeacherName }}
        </el-form-item>
        <el-form-item label="课程名称：" prop="leaveReason">
          {{ form.courseName }}
        </el-form-item>
        <el-form-item label="人数：" prop="leaveReason">
          {{ form.realNumber }} / {{ form.shouldNumber }}
        </el-form-item>
        <el-form-item label="点名老师：" prop="leaveReason">
          {{ form.callTeacher }}
        </el-form-item>
        <el-form-item label="点名时间：" prop="leaveReason">
          {{form.createTime }}
        </el-form-item>
        <el-form-item label="请假名单：" prop="leaveReason">
          <span v-for="(item,index) in form.beforeVacation" :key="index" style="color:#FF6206">
            {{ item.studentName }} <span v-if="index < form.beforeVacation.length-1">、</span>
          </span>
        </el-form-item>
      </el-form>
      <div class="call-md">
        <span class="title">上课学员名单</span>
        <el-table :data="form.callRecordList" style="width: 100%">
          <el-table-column label="姓名" align="center" prop="studentName" />
          <el-table-column label="到课状态" align="center">
            <template slot-scope="{row}">
              {{ row.type | typeFilter }}
            </template>
          </el-table-column>
          <el-table-column label="扣课时" align="center">
            <template slot-scope="{row}">
              <span v-if="row.type != 2">{{ row.classTimeNum }}</span>
              <span v-else>0</span>
              <span>课次</span>
            </template>
          </el-table-column>
          <el-table-column label="修改" align="center">
            <template slot-scope="{row}">
              <el-button type="text" @click="showRollcall(row.id)">修改</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 获取点名记录详细信息 -->
    <el-dialog title="修改点名" width="400px" :visible.sync='rollcallVisible' stripe>
      <el-divider />
      <el-form :model="rollcallInfo" label-width="100px" label-position="left">
        <el-form-item label="上课班级:">
          {{rollcallInfo.className}}
        </el-form-item>
        <el-form-item label="课程名称:">
          {{rollcallInfo.courseName}}
        </el-form-item>
        <el-form-item label="学员姓名:">
          {{rollcallInfo.studentName}}
        </el-form-item>
        <el-form-item label="上课时间:" prop="leaveReason">
          <span>{{ parseTime(rollcallInfo.classStartTime, '{m}-{d} {a} {h}:{i}:{s}')+'-'+parseTime(rollcallInfo.classEndTime, '{h}:{i}:{s}') }}</span>
        </el-form-item>
        <el-form-item label="到课状态:">
          <div class="state-box">
            <div class="state-item" :class="{'active':rollcallInfo.type === 0}" @click="rollcallInfo.type = 0">到课</div>
            <div class="state-item" :class="{'active':rollcallInfo.type === 1}" @click="rollcallInfo.type = 1">迟到</div>
            <div class="state-item" :class="{'active':rollcallInfo.type === 2}" @click="rollcallInfo.type = 2">请假</div>
            <div class="state-item" :class="{'active':rollcallInfo.type === 3}" @click="rollcallInfo.type = 3">未到</div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" style="width:100px;" @click="saveRollcall">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listRecord,
  getRecordInfo,
  exportRecord,
  getVacationList,
  getRollcallInfo,
  saveRollcall
} from '@/api/educenter/record'
export default {
  filters: {
    typeFilter(type) {
      var typeArr = ['到课', '迟到', '请假', '未到', '提前请假']
      return typeArr[type]
    }
  },
  props: {
    activeIndex: {
      type: Number,
      default: 1
    }
  },
  watch: {
    activeIndex: {
      handler(val) {
        console.log('调用了')
        if (val === 0) {
          this.queryParams.pageNum = 1
          this.getList()
        }
      },
      immediate: true
    }
  },
  components: {},
  data() {
    return {
      click: true,
      recordId: '',
      rollcallId: '',
      // 点名详细信息弹窗
      rollcallVisible: false,
      // 点名详细信息
      rollcallInfo: {},
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 上课记录表格数据
      recordList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: undefined,
        classId: undefined,
        courseId: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        classId: [
          {
            required: true,
            message: '对应班级不能为空',
            trigger: 'blur'
          }
        ],
        realNumber: [
          {
            required: true,
            message: '实到人数不能为空',
            trigger: 'blur'
          }
        ]
      },
      callRecord: false,
      tableData: []
    }
  },
  methods: {
    /** 查询上课记录列表 */
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      listRecord(query).then((response) => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 打开点名记录的弹窗
    onCllRecord(id) {
      this.recordId = id
      getRecordInfo(id).then((response) => {
        this.form = response.data
        var callRecordList = response.data.callRecordList
        this.form.callRecordList = callRecordList.filter((item) => {
          return item.type < 4
        })
        this.form.beforeVacation = callRecordList.filter((item) => {
          return item.type === 4
        })
        this.callRecord = true
      })
    },
    // 打开修改点名的弹窗
    showRollcall(id) {
      this.rollcallId = id
      getRollcallInfo(id).then((res) => {
        if (res.code === 200) {
          this.rollcallInfo = res.data
          this.rollcallVisible = true
        }
      })
    },
    // 修改到课状态
    saveRollcall() {
      if (this.click) {
        this.click = false
        saveRollcall({
          id: this.rollcallId,
          type: this.rollcallInfo.type
        })
          .then((res) => {
            if (res.code === 200) {
              this.msgSuccess('修改成功')
              this.rollcallVisible = false
              this.getList()
              this.onCllRecord(this.recordId)
            }
          })
          .finally(() => {
            this.click = true
          })
      }
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
    }
  },
  mounted() {}
}
</script>
<style lang="scss" scoped>
.state-box {
  display: flex;

  .state-item {
    padding: 0 5px;
    box-sizing: border-box;
    border: 1px solid #bbbbbb;
  }

  .active {
    border: 2px solid #ff6206 !important;
  }
}

.record {
  display: flex;
  flex-wrap: wrap;
  width: 100%;

  ::v-deep .el-form-item {
    width: 50%;
  }
}

.call-md {
  .title {
    margin-bottom: 20px;
    border-top: 2px solid #4f93fe;
    display: inline-block;
    padding-top: 10px;
  }
}
</style>