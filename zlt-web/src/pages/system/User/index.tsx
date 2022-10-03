import { user, exportUser, role } from '@/services/system/api';
import { ExportOutlined, ImportOutlined, PlusOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-components';
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
import { Space, Typography } from 'antd';
import { Button, Switch, message } from 'antd';
import React, { useState } from 'react';
import UpdateForm from './components/UpdateForm';

const { Link } = Typography;

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({});

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
      render: (value) => (value === 0 ? '男' : '女'),
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
          onClick={() => message.info('演示环境不支持该功能')}
        />
      ),
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
          <Link onClick={() => message.info('演示环境不支持该功能')}>重置密码</Link>
          <Link onClick={() => message.info('演示环境不支持该功能')}>删除</Link>
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
            <Button
              onClick={() => {
                message.info('演示环境不支持该功能');
              }}
            >
              <ImportOutlined /> 导入
            </Button>
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
        onFinish={async (values) => {
          console.log(values);
          message.info('演示环境不支持该功能');
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
            name="roleCodes"
            label="角色"
            width="md"
            request={async () => {
              const roles = await role();
              if (roles)
                return roles.map((r) => {
                  return { label: r.name, value: r.code };
                });
              return [];
            }}
            placeholder="选择角色"
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
    </PageContainer>
  );
};

export default TableList;
