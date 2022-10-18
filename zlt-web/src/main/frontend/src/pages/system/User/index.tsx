import {
  user,
  saveOrUpdateUser,
  exportUser,
  role,
  updateEnabled,
  resetPassword,
  deleteUser,
  importUser,
} from '@/services/system/api';
import { ExportOutlined, ImportOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ProFormRadio,
  ProFormSelect,
  ModalForm,
  PageContainer,
  ProFormText,
  ProForm,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import { Popconfirm, Space, Typography, Upload } from 'antd';
import { Button, Switch, message } from 'antd';
import type { RcFile } from 'antd/lib/upload';
import React, { useRef, useState } from 'react';
import UpdateForm from './components/UpdateForm';

const { Link } = Typography;

const handleAdd = async (fields: SYSTEM.User) => {
  const hide = message.loading('正在添加');
  try {
    const result = await saveOrUpdateUser({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('添加用户成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('添加用户失败');
    return false;
  }
};

const handleEdit = async (fields: SYSTEM.User) => {
  const hide = message.loading('正在更新');
  try {
    const result = await saveOrUpdateUser({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('修改用户成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('修改用户失败');
    return false;
  }
};

const swichUser = async (sysUser: SYSTEM.User) => {
  const hide = message.loading('正在更新');
  try {
    const result = await updateEnabled(sysUser.id, !sysUser.enabled);
    hide();
    if (result.resp_code === 0) {
      message.success('修改用户成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('修改用户失败');
    return false;
  }
};

const handleResetPassword = async (sysUser: SYSTEM.User) => {
  const hide = message.loading('正在更新');
  try {
    const result = await resetPassword(sysUser.id);
    hide();
    if (result.resp_code === 0) {
      message.success('重置密码成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('重置密码失败');
    return false;
  }
};

const handleDelete = async (sysUser: SYSTEM.User) => {
  const hide = message.loading('正在删除');
  try {
    const result = await deleteUser(sysUser.id);
    hide();
    if (result.resp_code === 0) {
      message.success('删除用户成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('删除用户失败');
    return false;
  }
};

const handleImport = async (file: RcFile) => {
  const hide = message.loading('正在导入');
  try {
    const formData = new FormData();
    formData.append('file', file);
    const result = await importUser(formData);
    hide();
    if (result.resp_code === 0) {
      message.success('导入用户成功');
    } else {
      message.error(result.resp_msg);
    }
  } catch (error) {
    hide();
    message.error('导入用户失败');
  }
};

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({});
  const actionRef = useRef<ActionType>();
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);

  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<SYSTEM.User>();

  const columns: ProColumns<SYSTEM.User>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
    },
    {
      title: '用户编号',
      dataIndex: 'id',
    },
    {
      title: '账号',
      dataIndex: 'username',
    },
    {
      title: '用户名',
      dataIndex: 'nickname',
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
    },
    {
      title: '性别',
      dataIndex: 'sex',
      renderText: (value) => (value === 0 ? '男' : '女'),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '状态',
      dataIndex: 'enabled',
      render: (_dom, entity) => (
        <Switch
          checkedChildren="正常"
          unCheckedChildren="锁定"
          checked={entity.enabled}
          onClick={async () => {
            const success = await swichUser(entity);
            if (success) {
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
          }}
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, entity) => (
        <Space>
          <Link
            onClick={() => {
              setCurrentRow(entity);
              handleUpdateModalVisible(true);
            }}
          >
            修改
          </Link>
          <Link onClick={async () => await handleResetPassword(entity)}>重置密码</Link>
          <Popconfirm
            title={`确认删除用户[${entity.username}]?`}
            onConfirm={async () => {
              const success = await handleDelete(entity);
              if (success) {
                if (actionRef.current) {
                  actionRef.current.reload();
                }
              }
            }}
          >
            <Link>删除</Link>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer header={{ subTitle: '管理系统中的用户账号' }}>
      <QueryFilter
        defaultCollapsed
        split
        className="query-filter"
        onFinish={async (values) => setParams(values)}
      >
        <ProFormSelect
          name="searchKey"
          label="搜索"
          valueEnum={{
            user_id: 'ID',
            username: '账号',
            nick_name: '用户名',
            mobile: '手机号',
          }}
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SYSTEM.User>
        rowKey="id"
        headerTitle="用户管理"
        actionRef={actionRef}
        request={user}
        columns={columns}
        search={false}
        params={params}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        toolBarRender={() => [
          <>
            <Button
              onClick={() => {
                exportUser();
              }}
            >
              <ExportOutlined /> 导出
            </Button>
            <Upload
              maxCount={1}
              action="/api-user/users/import"
              showUploadList={false}
              beforeUpload={async (file) => {
                await handleImport(file);
                return false;
              }}
            >
              <Button>
                <ImportOutlined /> 导入
              </Button>
            </Upload>

            <Button
              type="primary"
              onClick={() => {
                handleModalVisible(true);
              }}
            >
              <PlusOutlined /> 添加
            </Button>
          </>,
        ]}
      />
      <ModalForm
        title="添加用户"
        // layout="horizontal"
        visible={createModalVisible}
        onVisibleChange={handleModalVisible}
        initialValues={{ sex: 0 }}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          const roleIds: string[] = values.roleIds;
          const roleId = roleIds.join(',');
          values.roleId = roleId;
          const success = await handleAdd(values);
          if (success) {
            handleModalVisible(false);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
      >
        <ProForm.Group>
          <ProFormText
            rules={[
              {
                required: true,
                message: '账号不为空',
              },
            ]}
            width="md"
            name="username"
            label="账号"
            placeholder="输入账号"
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: '用户名不为空',
              },
            ]}
            width="md"
            name="nickname"
            label="用户名"
            placeholder="输入用户名"
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: '手机号不为空',
              },
            ]}
            width="md"
            name="mobile"
            label="手机号"
            placeholder="输入手机号"
          />
          <ProFormRadio.Group
            name="sex"
            label="性别"
            width="md"
            options={[
              {
                label: '男',
                value: 0,
              },
              {
                label: '女',
                value: 1,
              },
            ]}
          />
          <ProFormSelect
            mode="multiple"
            name="roleIds"
            label="角色"
            width="md"
            request={async () => {
              const roles = await role();
              if (roles)
                return roles.map((r) => {
                  return { label: r.name, value: r.id };
                });
              return [];
            }}
            placeholder="选择角色"
          />
        </ProForm.Group>
      </ModalForm>
      <UpdateForm
        onSubmit={async (values) => {
          if (currentRow) {
            const roleIds: number[] = values.roleIds || [];
            const roleId = roleIds.join(',');
            values.roleId = roleId;
            const success = await handleEdit({ ...currentRow, ...values });
            if (success) {
              handleUpdateModalVisible(false);
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
            setCurrentRow(undefined);
          }
        }}
        onVisibleChange={handleUpdateModalVisible}
        updateModalVisible={updateModalVisible}
        values={currentRow}
      />
    </PageContainer>
  );
};

export default TableList;
