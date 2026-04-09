<template>
  <div class="page permission">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="用户管理" name="user">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户列表</span>
              <el-button type="primary" :icon="Plus" @click="openUserDialog()">新增用户</el-button>
            </div>
          </template>
          <el-table v-loading="userLoading" :data="userList" stripe>
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="role" label="角色" width="140" />
            <el-table-column prop="dept" label="部门" width="120" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="row.status === '正常' ? 'success' : 'danger'" size="small">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="lastLogin" label="最后登录" width="160" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openUserDialog(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDeleteUser(row)">禁用</el-button>
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
          <el-table v-loading="roleLoading" :data="roleList" stripe>
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
      <el-tab-pane label="菜单权限" name="menu">
        <el-card shadow="never">
          <template #header>
            <span>菜单与功能权限</span>
          </template>
          <el-tree
            :data="menuTree"
            show-checkbox
            node-key="id"
            :default-expand-all="true"
            :props="{ label: 'name', children: 'children' }"
          />
          <div style="margin-top: 16px">
            <el-button type="primary" size="small">保存权限</el-button>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>
    <el-dialog v-model="userDialogVisible" :title="editingUserId ? '编辑用户' : '新增用户'" width="420px">
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="登录名" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.roleId" placeholder="请选择" style="width: 100%" clearable>
            <el-option v-for="r in roleList" :key="r.id" :label="r.name" :value="Number(r.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="部门">
          <el-input v-model="userForm.dept" placeholder="部门" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UserItem, RoleItem } from '@/api/permission'
import * as permissionApi from '@/api/permission'

const activeTab = ref('user')
const userLoading = ref(false)
const roleLoading = ref(false)
const userList = ref<UserItem[]>([])
const roleList = ref<RoleItem[]>([])

const userDialogVisible = ref(false)
const editingUserId = ref('')
const userSubmitting = ref(false)
const userForm = reactive({ username: '', roleId: null as number | null, dept: '', status: '正常' })

const roleDialogVisible = ref(false)
const editingRoleId = ref('')
const roleSubmitting = ref(false)
const roleForm = reactive({ name: '', code: '', desc: '', permissionCount: 0 })

const menuTree = ref([
  { id: '1', name: '工艺知识库', children: [{ id: '1-1', name: '查看' }, { id: '1-2', name: '新增/编辑' }, { id: '1-3', name: '删除' }] },
  { id: '2', name: '工艺设计过程', children: [{ id: '2-1', name: '查看任务' }, { id: '2-2', name: '新建任务' }, { id: '2-3', name: '取消/重试' }] },
  { id: '3', name: '工艺路径输出', children: [{ id: '3-1', name: '查看' }, { id: '3-2', name: '生成工艺路径' }, { id: '3-3', name: '导出' }] },
  { id: '4', name: '权限管理', children: [{ id: '4-1', name: '用户管理' }, { id: '4-2', name: '角色管理' }, { id: '4-3', name: '菜单权限' }] },
])

async function fetchUsers() {
  userLoading.value = true
  try {
    const res = await permissionApi.listUsers()
    userList.value = res.data ?? []
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

watch(activeTab, (tab) => {
  if (tab === 'user' && userList.value.length === 0) fetchUsers()
  if (tab === 'role' && roleList.value.length === 0) fetchRoles()
})

function openUserDialog(row?: UserItem) {
  editingUserId.value = row?.id ?? ''
  userForm.username = row?.username ?? ''
  userForm.roleId = row?.roleId != null ? Number(row.roleId) : null
  userForm.dept = row?.dept ?? ''
  userForm.status = row?.status ?? '正常'
  userDialogVisible.value = true
}

async function submitUser() {
  if (!userForm.username.trim()) {
    ElMessage.warning('请输入用户名')
    return
  }
  userSubmitting.value = true
  try {
    if (editingUserId.value) {
      await permissionApi.updateUser(editingUserId.value, {
        username: userForm.username,
        roleId: userForm.roleId ?? undefined,
        dept: userForm.dept,
        status: userForm.status,
      })
      ElMessage.success('更新成功')
    } else {
      await permissionApi.createUser({
        username: userForm.username,
        roleId: userForm.roleId ?? undefined,
        dept: userForm.dept,
        status: userForm.status,
      })
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
    await ElMessageBox.confirm(`确定禁用用户「${row.username}」？`, '提示', { type: 'warning' })
    await permissionApi.updateUser(row.id, { status: '禁用' })
    ElMessage.success('已禁用')
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

onMounted(() => {
  fetchUsers()
  fetchRoles()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
