import { pageRole } from '@/services/system/api';
import { PlusOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-components';
import {
  ProFormSelect,
  ModalForm,
  PageContainer,
  ProFormText,
  ProForm,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import { Space, Typography } from 'antd';
import { Button, message } from 'antd';
import React, { useState } from 'react';
import AssignAuth from './components/AssignAuth';
import UpdateForm from './components/UpdateForm';

const { Link } = Typography;

type AssignAuthProps = {
  roleId?: number;
  tenantId?: string;
  assignModalVisible: boolean;
};

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({});

  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);

  const [currentRow, setCurrentRow] = useState<SYSTEM.User>();

  const [assignAuth, setAssignAuth] = useState<AssignAuthProps>({ assignModalVisible: false });

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
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Link
            onClick={() => {
              handleUpdateModalVisible(true);
              setCurrentRow(record);
            }}
          >
            修改
          </Link>
          <Link onClick={() => message.info('演示环境不支持该功能')}>删除</Link>
          <Link
            onClick={() => {
              setAssignAuth({ roleId: record.id, assignModalVisible: true, tenantId: 'webApp' });
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
        className="query-filter"
        onFinish={async (values) => setParams(values)}
      >
        <ProFormSelect
          name="tenantId"
          label="所属应用"
          valueEnum={{
            webApp: 'pc端',
            app: '移动端',
            zlt: '第三方应用',
          }}
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
        onFinish={async () => {
          message.info('演示环境不支持该功能');
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
        </ProForm.Group>
      </ModalForm>
      <UpdateForm
        onSubmit={async () => {
          message.info('演示环境不支持该功能');
          handleUpdateModalVisible(false);
          setCurrentRow(undefined);
        }}
        onCancel={(flag) => {
          handleUpdateModalVisible(flag ?? true);
          setCurrentRow(undefined);
        }}
        updateModalVisible={updateModalVisible}
        values={currentRow || {}}
      />
      <AssignAuth {...assignAuth} onCancel={() => setAssignAuth({ assignModalVisible: false })} />
    </PageContainer>
  );
};

export default TableList;
