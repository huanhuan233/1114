<template>
  <div class="page system-management">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="user">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户列表</span>
              <el-button type="primary" :icon="Plus" @click="openUserDialog()">新增用户</el-button>
            </div>
          </template>
          <el-table v-loading="userLoading" :data="userList" stripe style="width: 100%">
            <el-table-column label="工号" min-width="100">
              <template #default="{ row }">{{ row.employeeNo || row.employee_no || '—' }}</template>
            </el-table-column>
            <el-table-column label="姓名" min-width="100">
              <template #default="{ row }">{{ row.realName || row.real_name || row.username || '—' }}</template>
            </el-table-column>
            <el-table-column label="人员类别" min-width="100">
              <template #default="{ row }">{{ row.personnelCategory || row.personnel_category || '—' }}</template>
            </el-table-column>
            <el-table-column label="岗位类型" min-width="100">
              <template #default="{ row }">{{ row.positionType || row.position_type || '—' }}</template>
            </el-table-column>
            <el-table-column label="职务" min-width="100">
              <template #default="{ row }">{{ row.jobTitle || row.job_title || '—' }}</template>
            </el-table-column>
            <el-table-column label="所属部门" min-width="110">
              <template #default="{ row }">{{ row.dept || '—' }}</template>
            </el-table-column>
            <el-table-column label="系统角色" min-width="110">
              <template #default="{ row }">{{ row.role || '—' }}</template>
            </el-table-column>
            <el-table-column label="联系方式" min-width="120">
              <template #default="{ row }">{{ row.contact || '—' }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="登陆时间" width="160">
              <template #default="{ row }">{{ row.loginTime || row.login_time || '—' }}</template>
            </el-table-column>
            <el-table-column label="登出时间" width="160">
              <template #default="{ row }">{{ row.logoutTime || row.logout_time || '—' }}</template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openUserDialog(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDeleteUser(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="角色管理" name="role">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>角色列表</span>
              <el-button type="primary" :icon="Plus" @click="openRoleDialog()">新增角色</el-button>
            </div>
          </template>
          <el-table v-loading="roleLoading" :data="roleList" stripe style="width: 100%">
            <el-table-column prop="name" label="角色名称" width="140" />
            <el-table-column prop="code" label="角色编码" width="140" />
            <el-table-column prop="desc" label="描述" min-width="200" />
            <el-table-column prop="permissionCount" label="权限数" width="90" align="center" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openRoleDialog(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDeleteRole(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="权限配置" name="permission">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>按角色配置菜单与功能权限</span>
              <el-button type="primary" size="small" :loading="permissionSaving" @click="saveRolePermission">保存当前角色权限</el-button>
            </div>
          </template>
          <el-form :inline="true" class="permission-form">
            <el-form-item label="选择角色">
              <el-select v-model="selectedRoleId" placeholder="请选择角色" style="width: 200px" @change="onRoleChangeForPermission">
                <el-option v-for="r in roleList" :key="r.id" :label="r.name" :value="r.id" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <span class="permission-hint">为不同角色勾选其可访问的菜单与功能，保存后生效。</span>
            </el-form-item>
          </el-form>
          <el-tree
            ref="permissionTreeRef"
            :data="menuTree"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :props="{ label: 'name', children: 'children' }"
          />
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="组织机构管理" name="org">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>组织机构</span>
              <el-button type="primary" :icon="Plus" size="small" @click="openOrgDialog()">新增部门</el-button>
            </div>
          </template>
          <el-tree
            v-loading="orgLoading"
            :data="orgTree"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            default-expand-all
            class="org-tree"
          >
            <template #default="{ node, data }">
              <span class="org-node">
                <span class="org-node-main">
                  <span>{{ data.shortName || data.name }}</span>
                  <span class="org-code" v-if="data.code">（{{ data.code }}）</span>
                  <span class="org-meta" v-if="data.orgType || (data.headCount != null && data.headCount >= 0)">
                    — <template v-if="data.orgType">{{ data.orgType }}</template><template v-if="data.orgType && data.headCount != null">，</template><template v-if="data.headCount != null">{{ data.headCount }}人</template>
                  </span>
                </span>
                <el-button link type="primary" size="small" @click.stop="openOrgDialog(data)">编辑</el-button>
                <el-button
                  v-if="data.parentId != null && data.code !== 'ROOT'"
                  link
                  type="danger"
                  size="small"
                  :icon="Delete"
                  @click.stop="handleDeleteOrg(data)"
                >
                  删除
                </el-button>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="日志追踪" name="log">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>操作日志</span>
            </div>
          </template>
          <div class="log-stats" v-if="logStats">
            <template v-if="logStats.startTime && logStats.endTime">
              <div class="log-stats-range">统计时间范围：{{ logStats.startTime }} 至 {{ logStats.endTime }}</div>
            </template>
            <span>总计运行时间：<strong>{{ formatRunTime(logStats.totalRunTimeSeconds) }}</strong> 秒</span>
            <span class="log-stats-sep">系统实际运行时间：<strong>{{ formatRunTime(logStats.systemActualRunTimeSeconds) }}</strong> 秒（至少有一人在线即计）</span>
          </div>
          <el-form :inline="true" class="log-form">
            <el-form-item label="用户">
              <el-input v-model="logFilter.user" placeholder="用户名" clearable style="width: 120px" />
            </el-form-item>
            <el-form-item label="模块">
              <el-select v-model="logFilter.module" placeholder="全部" clearable style="width: 140px">
                <el-option label="工艺知识库" value="工艺知识库" />
                <el-option label="任务管理" value="任务管理" />
                <el-option label="系统管理" value="系统管理" />
                <el-option label="工艺路径" value="工艺路径" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作">
              <el-input v-model="logFilter.action" placeholder="操作类型" clearable style="width: 120px" />
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="logFilter.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="YYYY-MM-DD"
                style="width: 240px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="fetchLogs">查询</el-button>
              <el-button @click="resetLogFilter">重置</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="filteredLogs" stripe style="width: 100%">
            <el-table-column prop="time" label="时间" width="170" />
            <el-table-column prop="username" label="用户" width="100" />
            <el-table-column prop="module" label="模块" width="120" />
            <el-table-column prop="action" label="操作" width="120" />
            <el-table-column prop="result" label="结果" width="80">
              <template #default="{ row }">
                <el-tag :type="row.result === '成功' ? 'success' : 'danger'" size="small">{{ row.result }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="ip" label="IP" width="120" />
            <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
          </el-table>
          <el-pagination
            v-model:current-page="logPage"
            :page-size="10"
            :total="filteredLogs.length"
            layout="total, prev, pager, next"
            class="pagination"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>
    <el-dialog v-model="userDialogVisible" :title="editingUserId ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="userForm" label-width="90px">
        <el-form-item label="工号">
          <el-input v-model="userForm.employeeNo" placeholder="如：E001" />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="userForm.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="登录名" />
        </el-form-item>
        <el-form-item label="人员类别">
          <el-select v-model="userForm.personnelCategory" placeholder="请选择" clearable style="width: 100%">
            <el-option label="正式在编" value="正式在编" />
            <el-option label="外聘" value="外聘" />
            <el-option label="劳务派遣" value="劳务派遣" />
            <el-option label="来宾" value="来宾" />
          </el-select>
        </el-form-item>
        <el-form-item label="岗位类型">
          <el-select v-model="userForm.positionType" placeholder="请选择" clearable style="width: 100%">
            <el-option label="技术" value="技术" />
            <el-option label="管理" value="管理" />
            <el-option label="生产" value="生产" />
            <el-option label="质量" value="质量" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-input v-model="userForm.jobTitle" placeholder="如：工艺工程师" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-select v-model="userForm.dept" placeholder="请选择部门" clearable style="width: 100%">
            <el-option v-for="opt in userDeptOptions" :key="opt.id" :label="opt.name" :value="opt.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="系统角色">
          <el-select v-model="userForm.roleId" placeholder="请选择" style="width: 100%" clearable>
            <el-option v-for="r in roleList" :key="r.id" :label="r.name" :value="Number(r.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="userForm.contact" placeholder="电话或邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="userForm.status" style="width: 100%">
            <el-option label="正常" value="正常" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="userSubmitting" @click="submitUser">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="roleDialogVisible" :title="editingRoleId ? '编辑角色' : '新增角色'" width="420px">
      <el-form :model="roleForm" label-width="90px">
        <el-form-item label="角色名称" required>
          <el-input v-model="roleForm.name" placeholder="如：工艺工程师" />
        </el-form-item>
        <el-form-item label="角色编码" required>
          <el-input v-model="roleForm.code" placeholder="如：process_engineer" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="roleForm.desc" type="textarea" :rows="2" placeholder="权限说明" />
        </el-form-item>
        <el-form-item label="权限数">
          <el-input-number v-model="roleForm.permissionCount" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSubmitting" @click="submitRole">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="orgDialogVisible" title="部门信息" width="460px">
      <el-form :model="orgForm" label-width="100px">
        <el-form-item label="部门名称" required>
          <el-input v-model="orgForm.name" placeholder="如：工艺部" />
        </el-form-item>
        <el-form-item label="部门简称">
          <el-input v-model="orgForm.shortName" placeholder="如：工艺部" />
        </el-form-item>
        <el-form-item label="部门编号">
          <el-input v-model="orgForm.code" placeholder="如：PROCESS" />
        </el-form-item>
        <el-form-item label="部门类型">
          <el-select v-model="orgForm.orgType" placeholder="请选择" clearable style="width: 100%">
            <el-option label="公司" value="公司" />
            <el-option label="部门" value="部门" />
            <el-option label="科室" value="科室" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级部门">
          <el-select v-model="orgForm.parentId" placeholder="无（顶级）" clearable style="width: 100%">
            <el-option v-for="n in flatOrgOptions" :key="n.id" :label="n.name" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门人数">
          <el-input-number v-model="orgForm.headCount" :min="0" :max="9999" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="orgDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOrg">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed, nextTick } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ElTree } from 'element-plus'
import type { UserItem, RoleItem } from '@/api/permission'
import type { OrgNode } from '@/api/org'
import * as permissionApi from '@/api/permission'
import * as orgApi from '@/api/org'

const activeTab = ref('user')
const userLoading = ref(false)
const roleLoading = ref(false)
const userList = ref<UserItem[]>([])
const roleList = ref<RoleItem[]>([])

const userDialogVisible = ref(false)
const editingUserId = ref('')
const userSubmitting = ref(false)
const userForm = reactive({
  employeeNo: '',
  realName: '',
  username: '',
  personnelCategory: '',
  positionType: '',
  jobTitle: '',
  dept: '',
  roleId: null as number | null,
  contact: '',
  status: '正常',
})

const roleDialogVisible = ref(false)
const editingRoleId = ref('')
const roleSubmitting = ref(false)
const roleForm = reactive({ name: '', code: '', desc: '', permissionCount: 0 })

const menuTree = ref<{ id: string; name: string; children?: { id: string; name: string }[] }[]>([])

const selectedRoleId = ref('')
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const permissionSaving = ref(false)
const rolePermissionCache = ref<Record<string, string[]>>({})

const orgLoading = ref(false)
const orgTree = ref<OrgNode[]>([])
const orgDialogVisible = ref(false)
const orgForm = reactive({
  id: '',
  name: '',
  shortName: '',
  code: '',
  orgType: '',
  parentId: '' as string,
  headCount: 0,
})

const logList = ref([
  { id: '1', time: '2025-03-05 14:32:10', username: 'admin', module: '系统管理', action: '登录', result: '成功', ip: '192.168.1.100', remark: '' },
  { id: '2', time: '2025-03-05 14:28:05', username: 'zhangsan', module: '工艺知识库', action: '新增', result: '成功', ip: '192.168.1.101', remark: '车削工艺规范' },
  { id: '3', time: '2025-03-05 14:15:22', username: 'admin', module: '系统管理', action: '编辑角色', result: '成功', ip: '192.168.1.100', remark: '工艺工程师' },
  { id: '4', time: '2025-03-05 13:50:00', username: 'lisi', module: '任务管理', action: '新建总任务', result: '成功', ip: '192.168.1.102', remark: '复材成型任务' },
  { id: '5', time: '2025-03-05 13:45:18', username: 'zhangsan', module: '工艺路径', action: '导出', result: '成功', ip: '192.168.1.101', remark: '' },
])
const logFilter = reactive({ user: '', module: '', action: '', dateRange: null as [string, string] | null })
const logPage = ref(1)
const logStats = ref<{ totalRunTimeSeconds: number; systemActualRunTimeSeconds: number; startTime?: string; endTime?: string } | null>(null)

const flatOrgOptions = computed(() => {
  const flat: { id: string; name: string }[] = []
  const excludeId = orgForm.id
  function walk(nodes: OrgNode[]) {
    nodes.forEach(n => {
      const sid = String(n.id)
      if (sid !== excludeId) flat.push({ id: sid, name: n.name })
      if (n.children?.length) walk(n.children)
    })
  }
  walk(orgTree.value)
  return flat
})

/** 用户表单用：全部组织机构（树展平为 name 列表），用于部门下拉 */
const userDeptOptions = computed(() => {
  const flat: { id: string; name: string }[] = []
  function walk(nodes: OrgNode[]) {
    nodes.forEach(n => {
      flat.push({ id: String(n.id), name: n.name })
      if (n.children?.length) walk(n.children)
    })
  }
  walk(orgTree.value)
  return flat
})

const filteredLogs = computed(() => {
  let list = logList.value
  if (logFilter.user) list = list.filter(l => l.username.includes(logFilter.user))
  if (logFilter.module) list = list.filter(l => l.module === logFilter.module)
  if (logFilter.action) list = list.filter(l => l.action.includes(logFilter.action))
  if (logFilter.dateRange?.length === 2) {
    const [s, e] = logFilter.dateRange
    list = list.filter(l => l.time >= s && l.time.slice(0, 10) <= e)
  }
  return list
})

async function fetchUsers() {
  userLoading.value = true
  try {
    const res = await permissionApi.listUsers()
    const raw = res.data
    userList.value = Array.isArray(raw) ? raw : (raw && typeof raw === 'object' && Array.isArray((raw as { list?: unknown[] }).list) ? (raw as { list: unknown[] }).list : [])
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载用户失败')
    userList.value = []
  } finally {
    userLoading.value = false
  }
}

async function fetchRoles() {
  roleLoading.value = true
  try {
    const res = await permissionApi.listRoles()
    roleList.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载角色失败')
    roleList.value = []
  } finally {
    roleLoading.value = false
  }
}

async function fetchOrgTree() {
  orgLoading.value = true
  try {
    const res = await orgApi.getOrgTree()
    orgTree.value = res.data ?? []
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '加载组织机构失败')
    orgTree.value = []
  } finally {
    orgLoading.value = false
  }
}

watch(activeTab, async (tab) => {
  if (tab === 'user') {
    if (userList.value.length === 0) fetchUsers()
    if (orgTree.value.length === 0) fetchOrgTree()
  }
  if (tab === 'role' && roleList.value.length === 0) fetchRoles()
  if (tab === 'org') fetchOrgTree()
  if (tab === 'log') fetchLogs()
  if (tab === 'permission') {
    if (roleList.value.length === 0) await fetchRoles()
    if (menuTree.value.length === 0) await fetchMenuTree()
    if (roleList.value.length > 0 && !selectedRoleId.value) {
      selectedRoleId.value = roleList.value[0].id
      nextTick(() => onRoleChangeForPermission())
    }
  }
})

function openUserDialog(row?: UserItem) {
  editingUserId.value = row?.id ?? ''
  userForm.employeeNo = row?.employeeNo ?? ''
  userForm.realName = row?.realName ?? ''
  userForm.username = row?.username ?? ''
  userForm.personnelCategory = row?.personnelCategory ?? ''
  userForm.positionType = row?.positionType ?? ''
  userForm.jobTitle = row?.jobTitle ?? ''
  userForm.dept = row?.dept ?? ''
  userForm.roleId = row?.roleId != null ? Number(row.roleId) : null
  userForm.contact = row?.contact ?? ''
  userForm.status = row?.status ?? '正常'
  if (orgTree.value.length === 0) fetchOrgTree()
  userDialogVisible.value = true
}

async function submitUser() {
  if (!userForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!userForm.realName.trim()) {
    ElMessage.warning('请输入姓名')
    return
  }
  userSubmitting.value = true
  const payload = {
    username: userForm.username,
    employeeNo: userForm.employeeNo || undefined,
    realName: userForm.realName || undefined,
    personnelCategory: userForm.personnelCategory || undefined,
    positionType: userForm.positionType || undefined,
    jobTitle: userForm.jobTitle || undefined,
    roleId: userForm.roleId ?? undefined,
    dept: userForm.dept || undefined,
    contact: userForm.contact || undefined,
    status: userForm.status,
  }
  try {
    if (editingUserId.value) {
      await permissionApi.updateUser(editingUserId.value, payload)
      ElMessage.success('更新成功')
    } else {
      await permissionApi.createUser(payload)
      ElMessage.success('新增成功')
    }
    userDialogVisible.value = false
    fetchUsers()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '操作失败')
  } finally {
    userSubmitting.value = false
  }
}

async function handleDeleteUser(row: UserItem) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」？`, '提示', { type: 'warning' })
    await permissionApi.deleteUser(row.id)
    ElMessage.success('已删除')
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e instanceof Error ? e.message : '操作失败')
  }
}

function openRoleDialog(row?: RoleItem) {
  editingRoleId.value = row?.id ?? ''
  roleForm.name = row?.name ?? ''
  roleForm.code = row?.code ?? ''
  roleForm.desc = row?.desc ?? ''
  roleForm.permissionCount = row?.permissionCount ?? 0
  roleDialogVisible.value = true
}

async function submitRole() {
  if (!roleForm.name.trim() || !roleForm.code.trim()) {
    ElMessage.warning('请填写角色名称和编码')
    return
  }
  roleSubmitting.value = true
  try {
    if (editingRoleId.value) {
      await permissionApi.updateRole(editingRoleId.value, roleForm)
      ElMessage.success('更新成功')
    } else {
      await permissionApi.createRole(roleForm)
      ElMessage.success('新增成功')
    }
    roleDialogVisible.value = false
    fetchRoles()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '操作失败')
  } finally {
    roleSubmitting.value = false
  }
}

async function handleDeleteRole(row: RoleItem) {
  try {
    await ElMessageBox.confirm(`确定删除角色「${row.name}」？`, '提示', { type: 'warning' })
    await permissionApi.deleteRole(row.id)
    ElMessage.success('已删除')
    fetchRoles()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function fetchMenuTree() {
  try {
    const res = await permissionApi.getMenuTree()
    menuTree.value = (res.data ?? []) as { id: string; name: string; children?: { id: string; name: string }[] }[]
  } catch {
    menuTree.value = []
  }
}

async function onRoleChangeForPermission() {
  if (!selectedRoleId.value) return
  const key = selectedRoleId.value
  const cached = rolePermissionCache.value[key]
  if (cached !== undefined) {
    nextTick(() => {
      if (permissionTreeRef.value) permissionTreeRef.value.setCheckedKeys(cached)
    })
    return
  }
  try {
    const res = await permissionApi.getRolePermissions(key)
    const ids = res.data ?? []
    rolePermissionCache.value[key] = ids
    nextTick(() => {
      if (permissionTreeRef.value) permissionTreeRef.value.setCheckedKeys(ids)
    })
  } catch {
    nextTick(() => {
      if (permissionTreeRef.value) permissionTreeRef.value.setCheckedKeys([])
    })
  }
}

async function saveRolePermission() {
  if (!selectedRoleId.value) {
    ElMessage.warning('请先选择角色')
    return
  }
  const half = permissionTreeRef.value?.getHalfCheckedKeys() ?? []
  const checked = permissionTreeRef.value?.getCheckedKeys() ?? []
  const keys = [...new Set([...half, ...checked])] as string[]
  rolePermissionCache.value[selectedRoleId.value] = keys
  permissionSaving.value = true
  try {
    await permissionApi.saveRolePermissions(selectedRoleId.value, keys)
    ElMessage.success('当前角色权限已保存')
    fetchRoles()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '保存失败')
  } finally {
    permissionSaving.value = false
  }
}

function openOrgDialog(node?: OrgNode) {
  orgForm.id = node?.id != null ? String(node.id) : ''
  orgForm.name = node?.name ?? ''
  orgForm.shortName = node?.shortName ?? ''
  orgForm.code = node?.code ?? ''
  orgForm.orgType = node?.orgType ?? ''
  const p = node?.parentId
  orgForm.parentId = p != null && p !== '' ? String(p) : ''
  orgForm.headCount = node?.headCount ?? 0
  orgDialogVisible.value = true
}

async function saveOrg() {
  if (!orgForm.name.trim()) {
    ElMessage.warning('请输入部门名称')
    return
  }
  try {
    const payload = {
      name: orgForm.name,
      shortName: orgForm.shortName || undefined,
      code: orgForm.code || undefined,
      orgType: orgForm.orgType || undefined,
      parentId: orgForm.parentId ? Number(orgForm.parentId) : null,
      headCount: orgForm.headCount ?? 0,
    }
    if (orgForm.id) {
      await orgApi.updateOrg(orgForm.id, payload)
      ElMessage.success('更新成功')
    } else {
      await orgApi.createOrg(payload)
      ElMessage.success('新增成功')
    }
    orgDialogVisible.value = false
    fetchOrgTree()
  } catch (e) {
    ElMessage.error(e instanceof Error ? e.message : '操作失败')
  }
}

async function handleDeleteOrg(data: OrgNode) {
  try {
    await ElMessageBox.confirm(`确定删除部门「${data.name}」？若该部门下有子部门则无法删除。`, '提示', { type: 'warning' })
    await orgApi.deleteOrg(String(data.id))
    ElMessage.success('删除成功')
    fetchOrgTree()
  } catch (e) {
    if ((e as Error)?.message !== 'cancel') {
      ElMessage.error(e instanceof Error ? e.message : '删除失败')
    }
  }
}

function formatRunTime(seconds: number) {
  if (seconds == null || seconds < 0) return '0'
  return String(seconds)
}

async function fetchLogs() {
  try {
    const params: { startTime?: string; endTime?: string } = {}
    if (logFilter.dateRange?.length === 2) {
      params.startTime = logFilter.dateRange[0]
      params.endTime = logFilter.dateRange[1]
    }
    const res = await permissionApi.getLogStats(params)
    logStats.value = res.data ?? null
    ElMessage.success(logFilter.dateRange?.length === 2 ? '已按时间范围统计运行时间' : '已加载运行时间统计（全部）')
  } catch (e) {
    logStats.value = null
    ElMessage.success('已按条件筛选（演示数据）')
  }
}

function resetLogFilter() {
  logFilter.user = ''
  logFilter.module = ''
  logFilter.action = ''
  logFilter.dateRange = null
  fetchLogs()
}

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<style scoped>
.page.system-management {
  width: 100%;
  min-height: 100%;
  box-sizing: border-box;
}
.page.system-management :deep(.el-tabs) {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.page.system-management :deep(.el-tabs__content) {
  flex: 1;
  overflow: hidden;
}
.page.system-management :deep(.el-tab-pane) {
  height: 100%;
}
.page.system-management :deep(.el-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.page.system-management :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.page.system-management :deep(.el-table) {
  width: 100% !important;
}
.permission-form {
  margin-bottom: 16px;
}
.permission-hint {
  font-size: 13px;
  color: #909399;
}
.org-tree {
  margin-top: 8px;
}
.org-node {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.org-code {
  font-size: 12px;
  color: #909399;
}
.log-stats {
  margin-bottom: 16px;
  padding: 10px 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
}
.log-stats-range {
  margin-bottom: 8px;
  font-size: 13px;
  color: #303133;
}
.log-stats-sep {
  margin-left: 24px;
}
.log-form {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
