import { appAll, deleteRole, pageRole, saveOrUpdateRole } from '@/services/system/api';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ProFormSelect,
  ModalForm,
  PageContainer,
  ProFormText,
  ProForm,
  ProTable,
  QueryFilter, ProFormRadio,
} from '@ant-design/pro-components';
import { Form, Popconfirm, Space, Typography } from 'antd';
import { Button, message } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import AssignAuth from './components/AssignAuth';
import UpdateForm from './components/UpdateForm';

const { Link } = Typography;

type AssignAuthProps = {
  roleId?: number;
  tenantId?: string;
  assignModalVisible: boolean;
};

const handleAdd = async (fields: SYSTEM.Role) => {
  const hide = message.loading('正在添加');
  try {
    const result = await saveOrUpdateRole({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('添加角色成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('添加角色失败');
    return false;
  }
};

const handleEdit = async (fields: SYSTEM.Role) => {
  const hide = message.loading('正在更新');
  try {
    const result = await saveOrUpdateRole({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('修改角色成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('修改角色失败');
    return false;
  }
};

const handleDelete = async (data: SYSTEM.Role) => {
  const hide = message.loading('正在删除');
  try {
    const result = await deleteRole(data.id);
    hide();
    if (result.resp_code === 0) {
      message.success('删除角色成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('删除角色失败');
    return false;
  }
};

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({});
  const [form] = Form.useForm();
  const actionRef = useRef<ActionType>();
  const [apps, setApps] = useState<SYSTEM.App[]>([]);
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);

  const [currentRow, setCurrentRow] = useState<SYSTEM.Role>();

  const [assignAuth, setAssignAuth] = useState<AssignAuthProps>({ assignModalVisible: false });

  useEffect(() => {
    (async () => {
      const appData = (await appAll()) || [];
      setApps(appData);
      if (appData.length > 0) {
        const tenantId = appData[0].clientId;
        setParams({ tenantId });
        form.setFieldsValue({ tenantId });
      }
    })();
  }, [form]);

  const columns: ProColumns<SYSTEM.Role>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
    },
    {
      title: 'ID',
      dataIndex: 'id',
    },
    {
      title: '角色名',
      dataIndex: 'name',
    },
    {
      title: 'CODE',
      dataIndex: 'code',
    },
    {
      title: '数据权限',
      dataIndex: 'dataScope',
      renderText: (value) => (value === 'ALL' ? '全部' : '本人'),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, entity) => (
        <Space>
          <Link
            onClick={() => {
              handleUpdateModalVisible(true);
              setCurrentRow(entity);
            }}
          >
            修改
          </Link>
          <Popconfirm
            title={`确认删除用户[${entity.name}]?`}
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
          <Link
            onClick={() => {
              setAssignAuth({ roleId: entity.id, assignModalVisible: true, tenantId: 'webApp' });
            }}
          >
            权限分配
          </Link>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer header={{ subTitle: '管理系统中的角色' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={6}
        form={form}
        className="query-filter"
        onValuesChange={(changedValues, allValues) => {
          if (changedValues.tenantId) setParams(allValues);
        }}
        onFinish={async (values) => setParams(values)}
        onReset={() => {
          form.setFieldsValue({ tenantId: params.tenantId });
          setParams({ tenantId: params.tenantId });
        }}
      >
        <ProFormSelect
          name="tenantId"
          label="所属应用"
          allowClear={false}
          options={apps.map((item) => {
            return { label: item.clientId, value: item.clientId };
          })}
        />
        <ProFormSelect
          name="searchKey"
          label="搜索"
          valueEnum={{
            name: '角色名称',
          }}
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SYSTEM.Role>
        rowKey="id"
        headerTitle="角色管理"
        actionRef={actionRef}
        request={pageRole}
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
        title="添加角色"
        // layout="horizontal"
        width={500}
        visible={createModalVisible}
        onVisibleChange={handleModalVisible}
        initialValues={{ dataScope: 'ALL' }}
        modalProps={{ destroyOnClose: true }}
        onFinish={async (values) => {
          const success = await handleAdd(values as SYSTEM.Role);
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
                message: '角色名不为空',
              },
            ]}
            width="lg"
            name="name"
            label="角色名"
            placeholder="输入角色名"
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: 'Code不为空',
              },
            ]}
            width="lg"
            name="code"
            label="Code"
            placeholder="输入Code"
          />
          <ProFormRadio.Group
            name="dataScope"
            label="数据权限"
            width="lg"
            options={[
              {
                label: '全部',
                value: 'ALL',
              },
              {
                label: '本人',
                value: 'CREATOR',
              },
            ]}
          />
        </ProForm.Group>
      </ModalForm>
      <UpdateForm
        onSubmit={async (values) => {
          if (currentRow) {
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
      <AssignAuth {...assignAuth} onCancel={() => setAssignAuth({ assignModalVisible: false })} />
    </PageContainer>
  );
};

export default TableList;
