<template>
  <div>
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item prop="studentName">
        <el-input v-model="queryParams.studentName" placeholder="请输入学员姓名" clearable @keyup.enter.native="handleQuery" maxlength="10" show-word-limit />
      </el-form-item>
      <el-form-item prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择处理结果" clearable>
          <el-option label="待处理" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="未通过" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="vocationData" stripe>
      <el-table-column label="申请学员" align="center" prop="studentName" />
      <el-table-column label="请假时间" align="center" prop="startTime">
        <template slot-scope="{row}">
          <span> {{ parseTime(row.startTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          <span> - </span>
          <span>{{ parseTime(row.offTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" />
      <el-table-column label="处理时间" align="center" prop="disposeTime" />
      <el-table-column label="处理结果" align="center" prop="type">
        <template slot-scope="{row}">
          <span v-if="row.type === 0" style="color:#4F93FE">待处理</span>
          <span v-if="row.type === 1" style="color:#4FDD31">已通过</span>
          <span v-if="row.type === 2" style="color:#FEB62D">未通过</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <span @click="lookDetail(row)">
            <el-button v-if="row.type === 0" size="mini" type="text">处理</el-button>
            <el-button v-else size="mini" type="text">查看详情</el-button>
          </span>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    <el-dialog :title="title" :visible.sync="dialogVisible" width="30%">
      <el-divider />
      <el-form ref="form" :model="form" :rules="rules" label-width="120px" label-position="left">
        <el-form-item label="申请学员:">
          {{ form.studentName }}
        </el-form-item>
        <el-form-item label="请假开始时间:" prop="shouldNumber">
          {{ form.startTime }}
        </el-form-item>
        <el-form-item label="请假结束时间:" prop="classStartTime">
          {{ form.offTime }}
        </el-form-item>
        <el-form-item label="请假原因:" prop="leaveReason">
          {{ form.leaveReason }}
        </el-form-item>
        <el-form-item v-if="form.type === 0" label="处理:" prop="result">
          <el-select v-model="form.result" placeholder="请选择处理结果">
            <el-option label="通过申请" :value="1" />
            <el-option label="不通过申请" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item v-else label="处理结果:">
          <span v-if="form.type === 2" style="color:#FF6206">未通过</span>
          <span v-if="form.type === 1" style="color:#4FDD31">已通过</span>
        </el-form-item>
        <el-form-item v-if="form.type === 0 && form.result === 2" label="拒绝原因:" prop="refuseReason">
          <el-input v-model="form.refuseReason" placeholder="请输入拒绝原因" maxlength="30" show-word-limit />
        </el-form-item>
        <el-form-item v-if="form.type === 2" label="拒绝原因:">
          <span>{{ form.refuseReason }}</span>
        </el-form-item>
      </el-form>
      <div v-if="form.type === 0" slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" style="margin-right:50px">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getVacationInfo,
  editVacationInfo,
  getVacationList
} from '@/api/educenter/record'
export default {
  props: {
    activeIndex: {
      type: Number,
      default: 1
    }
  },
  watch: {
    activeIndex: {
      handler(val) {
        if (val === 1) {
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
      id: '',
      title: '',
      form: {},
      rules: {
        refuseReason: [
          {
            required: true,
            message: '拒绝原因不能为空',
            trigger: 'blur'
          }
        ],
        result: [
          {
            required: true,
            message: '处理结果不能为空',
            trigger: 'change'
          }
        ]
      },
      dialogVisible: false,
      vocationData: [],
      total: 0,
      loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        type: undefined,
        studentName: undefined
      }
    }
  },
  methods: {
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    reset() {
      this.form = {
        studentName: undefined,
        startTime: undefined,
        offTime: undefined,
        leaveReason: undefined,
        type: undefined,
        refuseReason: undefined
      }
      this.resetForm('form')
    },
    // 查看详情/处理
    lookDetail(row) {
      this.reset()
      this.id = row.id
      getVacationInfo(this.id).then((res) => {
        if (res.code === 200) {
          this.form = res.data
          console.log('详细信息', this.form)
          if (this.form.type !== 2) {
            this.title = '请假详情'
          } else {
            this.title = '申请请假'
          }
          this.dialogVisible = true
        }
      })
    },
    getList() {
      let query = { ...this.queryParams }
      for (var key in query) {
        if (query[key] === '' || query[key] === undefined) {
          delete query[key]
        }
      }
      this.loading = true
      getVacationList(query).then((res) => {
        this.vocationData = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    //审核请假申请
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          if (this.click) {
            this.click = false
            const data = {
              id: this.id,
              type: this.form.result
            }
            if (this.form.result === 2) {
              data.refuseReason = this.form.refuseReason
            }
            editVacationInfo(data)
              .then((res) => {
                if (res.code === 200) {
                  this.dialogVisible = false
                  this.msgSuccess('处理成功')
                  this.getList()
                }
              })
              .finally(() => {
                this.click = true
              })
          }
        }
      })
    }
  }
}
</script>

