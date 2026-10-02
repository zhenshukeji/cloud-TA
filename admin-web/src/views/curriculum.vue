<template>
  <div class="app-container">
    <el-form :model="filterParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item prop="courseName">
        <el-input v-model="filterParams.courseName" placeholder="请输入课程名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item prop="className">
        <el-input v-model="filterParams.className" placeholder="请输入班级名称" clearable size="small" @keyup.enter.native="handleQuery" maxlength="16" show-word-limit />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="mini" icon="el-icon-search" @click="onSearch">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        <el-button type="primary" size="mini" icon="el-icon-plus" @click="onAddBtn">新增课次</el-button>
      </el-form-item>
    </el-form>
    <div v-loading="loading" class="lessons-box">
      <FullCalendar class="panel" ref="fullCalendar" :options="calendarOptions"></FullCalendar>
    </div>
    <!-- 新增课次 -->
    <el-dialog title="新增课次" :visible.sync="addLessons.lessonsPopup" width="50%" class="lessons-popup">
      <el-divider />
      <div class="list">
        <div class="label">
          <i>*</i>上课日期：
        </div>
        <div class="input">
          <el-date-picker v-model="addLessons.date" type="date" placeholder="选择日期" @change="changeDate($event,1)" :picker-options="expireTimeOption">
          </el-date-picker>
        </div>
      </div>
      <div class="list">
        <div class="label">
          <i>*</i>上课时间：
        </div>
        <div class="input">
          <el-time-picker value-format="HH:mm:ss" is-range v-model="addLessons.time" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" placeholder="选择时间范围" @change="changeTime($event,1)">
          </el-time-picker>
        </div>
      </div>
      <div class="list">
        <div class="label">
          <i>*</i>上课班级：
        </div>
        <div class="input">
          <el-button type="primary" @click="xzScbj">请选择上课班级</el-button>
          已选择：{{className}}
        </div>
      </div>
      <div class="btn-center">
        <el-button type="primary" @click="onSaveCurriculum">保 存</el-button>
      </div>
    </el-dialog>
    <!-- 选择班级 -->
    <el-dialog title="选择班级" :visible.sync="selectData.selectPopup" width="50%" class="select-popup">
      <el-divider />
      <div class="select-filter">
        <el-input class="input" v-model="inputBj" placeholder="请输入内容"></el-input>
        <el-button type="primary" @click="searchBj">搜 索</el-button>
      </div>
      <el-table class="table" :data="selectAllClassData.rows" style="width: 100%">
        <el-table-column prop="date" label="单选" width="130" align="center">
          <template slot-scope="scope">
            <el-radio class="radio" v-model="radio" :label="scope.$index" @change.native="getCurrentRow(scope.row)">
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="className" align="center" label="班级名称" width="180">
        </el-table-column>
        <el-table-column prop="courseName" align="center" label="关联课程">
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination background layout="prev, pager, next" :total="selectAllClassData.total" @current-change="handleCurrentChange" :current-page.sync="currentPage">
        </el-pagination>
      </div>
      <!-- <div class="btn">
                <el-button type="primary">确 定</el-button>
            </div> -->
    </el-dialog>
    <!-- 课程点名 -->
    <el-dialog title="课程点名" :visible.sync="rollcallData.rollcallPopup" width="800px" class="rollcall-popup">
      <el-divider />
      <div class="rollcall-data" v-if="intoRollCallData">
        <div class="list">上课班级：{{intoRollCallData.classBO.className}}</div>
        <div class="list">上课时间：{{intoRollCallData.startInWeek}} — {{intoRollCallData.endInWeek}}</div>
        <div class="list">主教老师：{{intoRollCallData.classBO.bishopTeacherName}}</div>
        <div class="list">辅助老师：{{intoRollCallData.classBO.helpTeacherName}}</div>
        <div class="list">课程名称：{{intoRollCallData.classBO.courseName}}</div>
        <div class="list">人数：{{intoRollCallData.classBO.existStudents}}人</div>
        <div class="list list2">
          <div class="title">学员信息</div>
          <span v-for="(item,index) in intoRollCallData.vacateStus" :key="index">{{item.studentName}}<span v-if="index < intoRollCallData.vacateStus.length -1">、已请假</span></span>
        </div>
      </div>
      <el-table :data="rollcallArr" style="width: 100%">
        <el-table-column prop="studentName" label="学员名称" width="180">
        </el-table-column>
        <el-table-column prop="remainingNumber" label="剩余课次" width="180">
        </el-table-column>
        <el-table-column prop="address" align="center" label="到课状态">
          <template slot-scope="{row}">
            <!-- scope.row -->
            <div class="rollcall-state">
              <span @click="onDmState(0,row)" :class="row.type == 0 ? 'on' : ''">到课</span>
              <span @click="onDmState(1,row)" :class="row.type == 1 ? 'on' : ''">迟到</span>
              <span @click="onDmState(2,row)" :class="row.type == 2 ? 'on' : ''">请假</span>
              <span @click="onDmState(3,row)" :class="row.type == 3 ? 'on' : ''">未到</span>
            </div>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="address" label="操作">
                    <template slot-scope="scope">
                        <el-button type="danger">移出课次</el-button>
                    </template>
                </el-table-column> -->
      </el-table>
      <div class="rollcall-btn">
        <div class="txt">
          到课{{this.classNum.num1.length}}人 迟到{{this.classNum.num2.length}}人 请假{{this.classNum.num3.length}}人 未到{{this.classNum.num4.length}}人
        </div>
        <el-button @click="saveRollCall" class="sign_btn">完成点名</el-button>
      </div>
    </el-dialog>
    <!-- 编辑课次 -->
    <el-dialog title="编辑课次" :visible.sync="editData.editPopup" width="700px" class="edit-popup">
      <el-divider />
      <div class="list">
        <div class="label">
          <i>*</i>上课日期：
        </div>
        <div class="input">
          <el-date-picker v-model="editData.date" type="date" placeholder="选择日期" @change="changeDate($event,2)" :picker-options="pickerOptions">
          </el-date-picker>
        </div>
      </div>
      <div class="list">
        <div class="label">
          <i>*</i>上课时间：
        </div>
        <div class="input">
          <el-time-picker value-format="HH:mm:ss" is-range v-model="editData.time" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" placeholder="选择时间范围" @change="changeTime($event,2)">
          </el-time-picker>
        </div>
      </div>
      <div class="list">
        <div class="label">
          上课班级：
        </div>
        <div class="tet">
          {{ chooseClassName }}
        </div>
      </div>
      <div class="list">
        <div class="label">主教老师：</div>
        <div class="txt">
          {{dmData.bishopTeacherName}}
        </div>
      </div>
      <div class="list">
        <div class="label">辅助老师：</div>
        <div class="txt">
          {{dmData.helpTeacherName}}
        </div>
      </div>
      <div class="list">
        <div class="label">上课课程：</div>
        <div class="txt">
          {{dmData.courseName}}
        </div>
      </div>
      <el-button class="edit-btn" type="primary" @click="onEdit">保存</el-button>
    </el-dialog>
    <!-- 查看详情 -->
    <el-dialog title="查看详情" :visible.sync="detailsData.detailsPopup" width="50%" class="detailsData-popup">
      <el-divider />
      <div class="details-txt" v-if="dmData">
        <div class="list">
          <div class="label">上课日期：</div>
          <div class="txt">
            {{dmData.startInWeek}}
          </div>
        </div>
        <div class="list">
          <div class="label">上课时间：</div>
          <div class="txt">
            {{dmData.startInWeek}} — {{dmData.endInWeek.slice(10,16)}}
          </div>
        </div>
        <div class="list">
          <div class="label">上课班级：</div>
          <div class="txt">
            {{ chooseClassName }}
          </div>
        </div>
        <div class="list">
          <div class="label">主教老师：</div>
          <div class="txt">
            {{dmData.bishopTeacherName}}
          </div>
        </div>
        <div class="list">
          <div class="label">辅助老师：</div>
          <div class="txt">
            {{dmData.helpTeacherName}}
          </div>
        </div>
        <div class="list">
          <div class="label">上课课程：</div>
          <div class="txt">
            {{dmData.courseName}}
          </div>
        </div>
      </div>
      <div class="trainee">
        <div class="title">上课学员</div>
        <p>
          <span v-for="(item,index) in getInfoData.stuNameList" :key="index">{{item}} <span v-if="index < getInfoData.stuNameList.length-1">、</span> </span>
        </p>
      </div>
      <div class="btn-box">
        <el-button type="primary" @click="onEditPopup">编辑课次</el-button>
        <el-button type="danger" @click="onDelete">删除课次</el-button>
      </div>
    </el-dialog>
    <!-- 抽屉 -->
    <el-drawer title="查看课次" :visible.sync="drawer" :with-header="false" size="350px">
      <el-divider />
      <div class="drawer-content" v-if="drawerData">
        <div class="title">{{drawerTitle}}</div>
        <div class="time">（{{weekArr[drawerData.weekDay-1]}}）{{drawerData.startInWeek}} — {{sliceTxt(drawerData.endInWeek,5)}}</div>
        <div class="drawer-txt">
          <p>授课课程：{{drawerData.courseName}}</p>
          <p>主教老师：{{drawerData.bishopTeacherName}}</p>
          <p>辅助老师：{{drawerData.helpTeacherName}}</p>
          <p>上课人次：{{drawerData.existStudents}}</p>
        </div>
        <div class="drawer-btn">
          <el-button type="" @click="onDrawer(1)">点名</el-button>
          <el-button type="" @click="onDrawer(2)">调整课次</el-button>
          <el-button type="" @click="onDrawer(3)">查看详情</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import FullCalendar from '@fullcalendar/vue'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import listPlugin from '@fullcalendar/list'
import tippy from 'tippy.js'
import 'tippy.js/dist/tippy.css'
import 'tippy.js/themes/light.css'
import 'tippy.js/themes/light-border.css'
import 'tippy.js/themes/translucent.css'
import {
  selectAll,
  intoRollCall,
  rollCall,
  changeClassTime,
  deleteClassTime,
  getInfo,
  insertChangeClass,
  selectAllClass
} from '@/api/educenter/curriculum'
import { setInterval, setTimeout } from 'timers'

export default {
  name: 'curriculum',
  components: {
    FullCalendar
  },
  data() {
    return {
      rollCallClick: true,
      editClick: true,
      click: true,
      delClick: true,
      // 班级名字
      chooseClassName: '',
      loading: false,
      drawer: false,
      input: '',
      radio: '',
      filterParams: {
        courseName: '',
        className: ''
      },
      expireTimeOption: {
        disabledDate(date) {
          let flag = true
          // 今天可选
          let now = new Date()
          if (
            now.getFullYear() === date.getFullYear() &&
            now.getMonth() === date.getMonth() &&
            now.getDate() === date.getDate()
          ) {
            flag = false
          }
          return date.getTime() < Date.now() && flag
        }
      },
      addLessons: {
        lessonsPopup: false,
        date: '',
        time: '',
        options: [
          {
            value: '0',
            label: '音乐课'
          },
          {
            value: '1',
            label: '数学课'
          },
          {
            value: '2',
            label: '语文课'
          },
          {
            value: '3',
            label: '英语课'
          },
          {
            value: '4',
            label: '历史课'
          }
        ],
        value: '',
        classVal: ''
      },
      rollcallData: {
        rollcallPopup: false
      },
      editData: {
        editPopup: false
      },
      detailsData: {
        detailsPopup: false
      },
      selectData: {
        selectPopup: false
      },
      saveData: {},
      tableData: [],
      weekArr: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      // 排课
      DISABLED_COLOR: '#c5c8ce',
      USED_COLOR: '#19be6b',
      NORMAL_COLOR: '#2db7f5',
      calendarOptions: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin, listPlugin],
        initialView: 'listWeek',
        locale: 'zh',
        allDayDefault: false,
        firstDay: 1,
        // 时间轴间距
        slotMinTime: '01:00',
        slotMaxTime: '00:00',
        slotDuration: '00:30:00',
        defaultTimedEventDuration: '00:30',
        // 月视图，是否为指定周数高度，true 6周高度
        fixedWeekCount: false,
        // 月视图，是否显示非本月日期
        showNonCurrentDates: false,
        // 是否显示全天插槽
        allDaySlot: false,
        // 是否显示周六日
        weekends: true,
        dayMaxEvents: false,
        slotLabelFormat: {
          hour: 'numeric',
          minute: '2-digit',
          hour12: false
        },
        eventTimeFormat: {
          hour: 'numeric',
          minute: '2-digit',
          hour12: false
        },
        buttonText: {
          today: '今天',
          month: '月',
          week: '周',
          day: '日',
          list: '周列表'
        },
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'listWeek'
          // right: 'dayGridMonth,timeGridWeek,timeGridDay listWeek'
          // right: "timeGridWeek",
        },
        validRange: this.validRange,
        events: [],
        datesSet: this.datesSet,
        eventClick: this.handleEventClick,
        dateClick: this.handleDateClick,
        eventMouseEnter: this.handEventMouseover,
        customButtons: {
          prev: {
            // this overrides the prev button
            text: '上周',
            click: () => {
              console.log('上一页')
              let calendarApi = this.$refs.fullCalendar.getApi()
              calendarApi.prev()
              this.selectAll({
                num: --this.num,
                ...this.filterParams
              })
            }
          },
          next: {
            // this overrides the next button
            text: '下周',
            click: () => {
              console.log('下一页')

              let calendarApi = this.$refs.fullCalendar.getApi()
              calendarApi.next()
              this.selectAll({
                num: ++this.num,
                ...this.filterParams
              })
            }
          },
          today: {
            text: '今天',
            click: () => {
              let calendarApi = this.$refs.fullCalendar.getApi()
              calendarApi.today()
              console.log(calendarApi)
              this.num = 0
              this.selectAll({
                num: 0,
                ...this.filterParams
              })
            }
          }
        }
      },
      validRange: {
        start: '2019-01-01',
        end: this.$moment().add(6, 'months').format('YYYY-MM-DD')
      },
      users: [],
      params: {},
      viewVisible: false,
      selectId: 0,
      selectDate: '',
      saveVisible: false,
      selectAllData: '',
      num: 0,
      dmData: '',
      intoRollCallData: '',
      dmStateIndex: 0,
      rollcallArr: [],
      getInfoData: '',
      dmStudentId: '',
      selectAllClassData: '',
      classId: '',
      className: '',
      inputBj: '',
      classNum: {
        num1: [],
        num2: [],
        num3: [],
        num4: []
      },
      currentPage: 1,
      drawerData: '',
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
      isEdit: 0,
      pickerOptions: {
        disabledDate(date) {
          let flag = true
          // 今天可选
          let now = new Date()
          if (
            now.getFullYear() === date.getFullYear() &&
            now.getMonth() === date.getMonth() &&
            now.getDate() === date.getDate()
          ) {
            flag = false
          }
          return date.getTime() <= Date.now() && flag
        }
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        if (route.path === '/educenter/curriculum') {
          this.selectAll({
            num: this.num
          })
        }
      },
      immediate: true
    }
  },

  methods: {
    sliceTxt(txt, num) {
      return txt.slice(num)
    },
    handleCurrentChange(res) {
      this.selectAllClass({
        pageSize: 10,
        pageNum: res
      })
    },
    searchBj() {
      this.selectAllClass({
        pageSize: 10,
        pageNum: 1,
        className: this.inputBj
      })
    },
    getCurrentRow(row) {
      console.log(row)
      this.classId = row.id
      this.className = row.className
      this.addLessons.className = row.className
      this.selectData.selectPopup = false
    },
    xzScbj() {
      this.radio = ''
      this.currentPage = 1
      this.className = ''
      this.selectData.selectPopup = true
      this.selectAllClass({
        pageSize: 10,
        pageNum: 1
      })
    },
    onEditPopup() {
      this.editData.editPopup = true
    },
    onDmState(index, row) {
      this.$set(row, 'type', index)
      this.classNum.num1 = []
      this.classNum.num2 = []
      this.classNum.num3 = []
      this.classNum.num4 = []
      this.rollcallArr.map((arr, index) => {
        if (arr.type == 0) {
          this.classNum.num1.push(arr)
        }
        if (arr.type == 1) {
          this.classNum.num2.push(arr)
        }
        if (arr.type == 2) {
          this.classNum.num3.push(arr)
        }
        if (arr.type == 3) {
          this.classNum.num4.push(arr)
        }
      })
    },
    filterClass(item, key) {
      item.filter((item, key) => {
        console.log(item.type == key)
        // return item.ad_class == "N";
      })
    },
    getCalendarEvents(info, successCallback, failureCallback) {
      console.log(info)
      let events = [
        ...this.calendarEvents,
        {
          title: info.startStr,
          start: info.start.valueOf()
        }
      ]
      successCallback(events)
    },
    handleQuery() {},
    // 新增课次
    onAddBtn() {
      this.addLessons.date = ''
      this.addLessons.time = ''
      this.addLessons.className = ''
      this.className = ''
      this.addLessons.lessonsPopup = true
    },
    changeDate(row, index) {
      if (index == 1) {
        this.addLessons.date = this.$moment(row).format('YYYY-MM-DD')
      } else if (index == 2) {
        this.editData.date = this.$moment(row).format('YYYY-MM-DD')
      }
    },
    changeTime(row, index) {
      if (index == 1) {
        this.addLessons.time = row
        this.isEdit = 0
      } else if (index == 2) {
        this.editData.time = row
        this.isEdit = 1
      }
    },
    changeClass(row) {
      this.addLessons.classVal = row
    },
    onSaveCurriculum() {
      if (this.addLessons.date == '') {
        this.$message.error('请选择上课日期')
        return
      }
      if (this.addLessons.time == '') {
        this.$message.error('请选择上课时间')
        return
      }
      if (this.addLessons.className == '') {
        this.$message.error('请选择上课班级')
        return
      }
      this.insertChangeClass({
        clsStartTime: this.addLessons.date + ' ' + this.addLessons.time[0],
        clsEndTime: this.addLessons.date + ' ' + this.addLessons.time[1],
        classId: this.classId
      })
    },
    // 排课
    init() {
      // this.search();
    },
    search() {
      this.calendarOptions.events = []
      this.calendarOptions.events.push(
        {
          title: '数学一班',
          start: '2020-09-28 12:43',
          end: '2020-09-28 15:30',
          editable: false,
          color: '#78bffa'
        },
        {
          title: '语文一班',
          start: '2020-09-28 12:43',
          end: '2020-09-28 15:30',
          editable: false,
          color: '#b6a1f5'
        },
        {
          title: '语文一班',
          start: '2020-09-28 12:43',
          end: '2020-09-28 15:30',
          editable: false,
          color: '#fabb7c'
        }
      )
    },
    datesSet(info) {
      let date = info.view.activeStart
      // 查询月份数据
      this.params.startDate = this.$moment(date)
        .startOf('month')
        .format('YYYY-MM-DD HH:mm:ss')
      this.params.endDate = this.$moment(date)
        .endOf('month')
        .format('YYYY-MM-DD HH:mm:ss')
      // this.search();
    },
    handleDateClick(arg) {
      this.selectDate = this.$moment(arg.date)
        .startOf('day')
        .format('YYYY-MM-DD HH:mm:ss')
      this.saveVisible = true
    },
    handleEventClick(info) {
      // console.log(1);
      console.log(info)
      // this.selectId = info.event.extendedProps.id;
      this.drawer = true
      this.drawerTitle = info.event.title
      this.drawerData = info.event._def.extendedProps
      this.dmData = info.event._def.extendedProps
      this.chooseClassName = info.event.title
    },
    onDrawer(index) {
      this.drawer = false
      switch (index) {
        case 1:
          this.intoRollCall({
            classId: this.dmData.classId,
            startInWeek: this.dmData.startInWeek,
            endInWeek: this.dmData.endInWeek,
            weekDay: this.dmData.weekDay
          })
          break
        case 2:
          this.editData.editPopup = true
          //编辑回显
          this.$set(this.editData, 'date', this.dmData.startInWeek)
          this.$set(this.editData, 'time', [
            this.parseTime(this.dmData.startInWeek, '{h}:{i}:{s}'),
            this.parseTime(this.dmData.endInWeek, '{h}:{i}:{s}')
          ])
          console.log(this.editData.time)
          // this.getInfo({
          //     id: this.dmData.ids,
          //     classId: this.dmData.classId,
          //     idType: this.dmData.idType,
          //     num: this.num,
          // });
          break
        case 3:
          this.detailsData.detailsPopup = true
          this.getInfo({
            id: this.dmData.ids,
            classId: this.dmData.classId,
            idType: this.dmData.idType,
            num: this.num
          })
          break
        default:
      }
    },
    handEventMouseover(info) {
      // console.log(info.event);
      tippy(info.el, {
        content: '点击查看课次',
        // content: info.event.extendedProps.name,
        placement: 'top-start'
        // arrow: false,
        // 鼠标放在提示中提示不消失
        // interactive: true,
      })
      // let col = info.event.borderColor;
      // let eve = info.event._def.extendedProps;
      // this.dmData = info.event._def.extendedProps;
      // tippy(info.el, {
      //     theme: "light",
      //     content:
      //         "<div style='width: 300px;height:255px;'>" +
      //         "<div style='line-height: 30px;color:" +
      //         "#000;font-size=16px;" +
      //         "'>" +
      //         info.event.title +
      //         "</div>" +
      //         "<div style='border-bottom: 1px solid #CCCCCC;margin-bottom:10px;line-height: 30px;font-size:12px;'>" +
      //         "(周" +
      //         eve.weekDay +
      //         ")" +
      //         eve.startInWeek +
      //         " — " +
      //         eve.endInWeek +
      //         "</div>" +
      //         "<div style='color: #666666;line-height: 30px;'>授课课程：" +
      //         eve.courseName +
      //         "</div>" +
      //         "<div style='color: #666666;line-height: 30px;'>主教老师：" +
      //         eve.bishopTeacherName +
      //         "</div>" +
      //         "<div style='color: #666666;line-height: 30px;'>辅助老师：" +
      //         eve.helpTeacherName +
      //         "</div>" +
      //         "<div style='color: #666666;line-height: 30px;'>上课人次：" +
      //         eve.existStudents +
      //         "/" +
      //         eve.sumStudents +
      //         "</div>" +
      //         "<div class='xf-btn'>" +
      //         "<span class='dm'>点名</span>" +
      //         "<span class='tzkc'>调整课次</span>" +
      //         "<span class='ckxq'>查看详情</span>" +
      //         "</div>" +
      //         "</div>",
      //     // trigger: "click",
      //     interactive: true,
      //     // placement: "top-end",
      //     allowHTML: true,
      // });
    },
    onSearch() {
      for (var key in this.filterParams) {
        if (
          this.filterParams[key] === '' ||
          this.filterParams[key] === undefined
        ) {
          delete this.filterParams[key]
        }
      }
      this.selectAll({
        num: this.num,
        ...this.filterParams
      })
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.onSearch()
    },
    selectAll(data) {
      this.loading = true
      selectAll(data)
        .then((res) => {
          if (res.code === 200) {
            this.calendarOptions.events = []
            console.log('课表信息')
            this.selectAllData = res.data
            var list = []
            res.data.map((arr, index) => {
              this.calendarOptions.events.push({
                title: arr.className,
                courseName: arr.courseName,
                start: arr.startInWeek,
                end: arr.endInWeek,
                editable: false,
                color: this.colorArr[arr.courseColor],
                weekDay: arr.weekDay,
                bishopTeacherName: arr.bishopTeacherName,
                helpTeacherName: arr.helpTeacherName,
                startInWeek: arr.startInWeek,
                endInWeek: arr.endInWeek,
                ids: arr.id,
                classId: arr.classId,
                idType: arr.idType,
                existStudents: arr.existStudents,
                sumStudents: arr.sumStudents
              })
            })
          } else {
            this.msgError(res.msg)
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    intoRollCall(data) {
      intoRollCall(data)
        .then((res) => {
          if (res.code === 200) {
            this.intoRollCallData = res.data
            this.rollcallData.rollcallPopup = true
            this.rollcallArr = []
            res.data.courseTimeList.map((arr) => {
              this.rollcallArr.push(arr)
            })
          }
        })
        .catch((err) => {
          this.rollcallData.rollcallPopup = false
          // this.msgError('不可重复点名')
        })
    },
    saveRollCall() {
      // console.log(this.dmData)
      // console.log(this.intoRollCallData)
      var _arr = []
      var _arr2 = []
      if (this.rollcallArr.length === 0) {
        this.$message.error('不存在学员,无需点名')
        return
      }
      for (var i = 0; i < this.rollcallArr.length; i++) {
        if (this.rollcallArr[i].type == undefined) {
          this.$message.error('还有学员未选择到课状态')
          return
        }
        _arr.push({
          studentId: this.rollcallArr[i].studentId,
          type: this.rollcallArr[i].type
        })
      }
      if (this.intoRollCallData.vacateStus) {
        for (var i = 0; i < this.intoRollCallData.vacateStus.length; i++) {
          _arr2.push({
            studentId: this.intoRollCallData.vacateStus[i].studentId,
            type: this.intoRollCallData.vacateStus[i].type
          })
        }
      }

      this.rollCall({
        classVO: {
          id: this.dmData.classId
        },
        vacateStus: _arr2,
        startInWeek: this.dmData.startInWeek,
        endInWeek: this.dmData.endInWeek,
        callRecordList: _arr
      })
    },
    // 点名
    rollCall(data) {
      if (this.rollCallClick) {
        this.rollCallClick = false
        rollCall(data)
          .then((res) => {
            if (res.code === 200) {
              this.rollcallData.rollcallPopup = false
              this.$notify({
                title: '成功',
                message: '完成点名',
                type: 'success'
              })
            }
          })
          .finally(() => {
            this.rollCallClick = true
          })
      }
    },
    onEdit() {
      // console.log(this.editData.date)
      // console.log(this.editData.time)
      // console.log(this.dmData);

      if (this.isEdit == 1) {
        console.log(1)
        // console.log(this.editData.date.slice(0, 10));
        this.changeClassTime({
          id: this.dmData.ids,
          classId: this.dmData.classId,
          idType: this.dmData.idType,
          originalStartTime: this.dmData.startInWeek,
          originalEndTime: this.dmData.endInWeek,
          clsStartTime:
            this.editData.date.slice(0, 10) + ' ' + this.editData.time[0],
          clsEndTime:
            this.editData.date.slice(0, 10) + ' ' + this.editData.time[1]
        })
      } else {
        // console.log(0);
        // console.log(
        //     this.$moment(this.editData.time[0]).format("HH:MM")
        // );
        // console.log(
        //     this.$moment(this.editData.time[1]).format("HH:MM")
        // );
        // console.log(this.dmData)
        // console.log(this.dmData.startInWeek.slice(10))
        // console.log(this.editData.date);
        // return;
        this.changeClassTime({
          id: this.dmData.ids,
          classId: this.dmData.classId,
          idType: this.dmData.idType,
          originalStartTime: this.dmData.startInWeek,
          originalEndTime: this.dmData.endInWeek,
          clsStartTime:
            this.parseTime(this.editData.date, '{y}-{m}-{d}') +
            ' ' +
            this.editData.time[0],
          clsEndTime:
            this.parseTime(this.editData.date, '{y}-{m}-{d}') +
            ' ' +
            this.editData.time[1]
        })
      }
    },
    // 修改课次
    changeClassTime(data) {
      if (this.editClick) {
        this.editClick = false
        changeClassTime(data)
          .then((res) => {
            if (res.code === 200) {
              console.log('编辑课表信息中的课次')
              this.msgSuccess('编辑成功')
              this.selectAll({
                num: this.num,
                ...this.filterParams
              })
              this.editData.editPopup = false
              this.detailsData.detailsPopup = false
            } else {
              this.msgError(res.msg)
            }
          })
          .finally(() => {
            this.editClick = true
          })
      }
    },
    onDelete() {
      this.deleteClassTime({
        id: this.dmData.ids,
        classId: this.dmData.classId,
        idType: this.dmData.idType,
        startInWeek: this.dmData.startInWeek,
        endInWeek: this.dmData.endInWeek
      })
    },
    // 删除课次
    deleteClassTime(data) {
      if (this.delClick) {
        this.delClick = false
        deleteClassTime(data)
          .then((res) => {
            if (res.code === 200) {
              console.log('删除课表信息中的课次')
              this.msgSuccess('删除成功')
              this.selectAll({
                num: this.num,
                ...this.filterParams
              })
              this.detailsData.detailsPopup = false
            } else {
              this.msgError(res.msg)
            }
          })
          .finally(() => {
            this.delClick = true
          })
      }
    },
    getInfo(data) {
      getInfo(data).then((res) => {
        if (res.code === 200) {
          console.log('获取班级上课时间详细信息')
          this.getInfoData = res.data
          // this.editData.time = [
          //     new Date(2020, 9, 10, 8, 40),
          //     new Date(2020, 9, 10, 9, 40),
          // ];
          // this.editData.time[1] = res.data.clsEndTime;
          console.log(this.editData.time)
        } else {
          this.msgError(res.msg)
        }
      })
    },
    insertChangeClass(data) {
      if (this.click) {
        this.click = false
        insertChangeClass(data)
          .then((res) => {
            if (res.code === 200) {
              console.log('新增课表')
              this.addLessons.lessonsPopup = false
              this.selectAll({
                num: this.num,
                ...this.filterParams
              })
            } else {
              this.msgError(res.msg)
            }
          })
          .finally(() => {
            this.click = true
          })
      }
    },
    selectAllClass(data) {
      selectAllClass(data).then((res) => {
        if (res.code === 200) {
          console.log('获取班级')
          this.selectAllClassData = res
        } else {
          this.msgError(res.msg)
        }
      })
    }
  }
}
</script>
<style scoped>
#medicalUserWork .ivu-tag {
  display: block;
}
.tippy-box {
  box-shadow: initial !important;
  box-shadow: 0px 0px 5px #bfbfbf !important;
}
.fc .fc-button-primary {
  background: #fff;
  color: #666;
  border-color: #dce0e7;
}
.fc .fc-toolbar-title {
  font-weight: normal;
  font-size: 18px;
}
.xf-btn {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.xf-btn span {
  margin: 0 20px;
  width: 98px;
  height: 26px;
  display: flex;
  justify-content: center;
  align-items: center;
  /* background: #000; */
  color: #fff;
  font-size: 12px;
  border-radius: 3px;
}
.xf-btn .dm {
  background: #409eff;
}
.xf-btn .tzkc {
  background: #67c23a;
}
.xf-btn .ckxq {
  background: #e6a23c;
}
.fc .fc-button-primary:not(:disabled):active,
.fc .fc-button-primary:not(:disabled).fc-button-active {
  background-color: #1890ff;
  border-color: #1890ff;
}
.fc-v-event .fc-event-main-frame {
  display: flex;
  flex-direction: column;
  /* align-items: center; */
  justify-content: center;
}
.fc-v-event .fc-event-title {
  max-height: initial;
}
.fc-v-event .fc-event-title-container {
  flex-grow: initial;
}
.fc .fc-col-header-cell-cushion {
  padding: 15px 4px;
  font-weight: normal;
  color: #595959;
}
.fc-theme-standard td,
.fc-theme-standard th {
  border-color: #eaeaea;
}
.fc-theme-standard .fc-scrollgrid {
  border-color: #eaeaea;
}
.fc .fc-button-primary:disabled {
  background-color: #67c23a;
  border-color: #67c23a;
}
.fc-timegrid-event-harness {
  /* right: 35% !important; */
  margin-right: 0 !important;
}
</style>
<style lang="scss" scoped>
.filter-box {
  display: flex;
  margin-bottom: 30px;
  .el-input {
    width: 200px;
    margin-right: 20px;
  }
  ::v-deep .el-input--small .el-input__inner {
    height: 36px;
    line-height: 36px;
  }
}
.btn-center {
  display: flex;
  justify-content: center;
  margin-top: 75px;
}
.lessons-popup {
  .list {
    display: flex;
    align-items: center;
    margin-bottom: 22px;
    .label {
      margin-right: 83px;
      i {
        font-style: normal;
        color: rgb(234, 14, 14);
      }
    }
  }
}
.lessons-box {
  display: flex;
  width: 100%;
  height: auto;
}
.lessons-time {
  .list {
    width: 120px;
    height: 60px;
    border: solid 1px #bbbbbb;
    outline: solid 1px #bbbbbb;
    display: flex;
    justify-content: center;
    align-items: flex-end;
    position: relative;
    span {
      padding: 0 10px;
      background: #fff;
      position: absolute;
      bottom: -10px;
      z-index: 5;
    }
  }
}
.lessons-cycle {
  .title {
    width: 210px;
    height: 60px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: solid 1px #bbbbbb;
    outline: solid 1px #bbbbbb;
  }
  .list {
    width: 210px;
    height: 60px;
    padding: 10px;
    display: flex;
    border: solid 1px #bbbbbb;
    outline: solid 1px #bbbbbb;
    span {
      background: #82bad5;
      color: #fff;
      width: 100%;
      height: 100%;
      padding: 5px;
      font-size: 14px;
      display: flex;
      justify-content: center;
      align-items: center;
      margin: 0 5px;
      word-wrap: break-word;
      text-align: center;
    }
  }
}
.panel {
  width: 100%;
}
.rollcall-popup {
  .rollcall-data {
    display: flex;
    flex-wrap: wrap;
    .list {
      width: 50%;
      margin-bottom: 22px;
    }
    .list2 {
      color: rgba(255, 98, 6, 1);
      .title {
        width: 64px;
        text-align: center;
        border-top: 1px solid #4f93fe;
        // margin-bottom: 20px;
        padding-top: 10px;
        color: #606266;
      }
      span {
        color: rgba(255, 98, 6, 1);
      }
    }
  }
  .rollcall-btn {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    margin-top: 44px;
    .txt {
      margin-right: 38px;
    }
    .sign_btn {
      border: none;
      background: #ff6206;
      color: #ffffff;
      box-shadow: 0px 2px 6px 0px rgba(0, 0, 0, 0.4);
    }
  }
  .rollcall-state {
    display: flex;
    justify-content: center;
    span {
      width: 55px;
      height: 35px;
      display: flex;
      justify-content: center;
      align-items: center;
      border: solid 1px #cccccc;
    }
    .on {
      border-color: #ff6206;
      // color: #ff6206;
      background: #ff6206;
      color: #fff;
    }
  }
}
.edit-popup {
  .list {
    display: flex;
    align-items: center;
    margin-bottom: 22px;
    .label {
      margin-right: 80px;
      i {
        font-style: normal;
        color: rgb(234, 14, 14);
      }
    }
  }
  .edit-btn {
    display: block;
    margin: 58px auto 0 auto;
  }
}
.detailsData-popup {
  .details-txt {
    display: flex;
    flex-wrap: wrap;
    .list {
      display: flex;
      width: 50%;
      margin-bottom: 25px;
    }
  }
  .trainee {
    .title {
      border-top: solid 1px #9cbbbbbb;
      padding-top: 13px;
      width: 72px;
      text-align: center;
    }
    p {
      line-height: 28px;
    }
  }
  .btn-box {
    display: flex;
    justify-content: space-around;
    margin-top: 46px;
  }
}
.select-popup {
  .select-filter {
    margin-bottom: 40px;
    display: flex;
    .input {
      width: 200px;
      margin-right: 36px;
    }
  }
  .table {
    margin-bottom: 30px;
  }
  .pagination {
    display: flex;
    justify-content: flex-end;
  }
  .btn {
    display: flex;
    justify-content: flex-end;
    margin-top: 40px;
  }
}
.drawer-content {
  padding: 30px;
  .title {
    margin-bottom: 10px;
    font-size: 18px;
  }
  .time {
    font-size: 14px;
    color: #666;
  }
  .drawer-txt {
    margin: 50px 0;
  }
  .drawer-btn {
    display: flex;
    justify-content: space-between;
    button {
      width: 78px;
      border: none;
      background: none;
      color: #1890ff;
    }
    .el-button--medium {
      padding: 10px 0;
    }
  }
}
</style>
