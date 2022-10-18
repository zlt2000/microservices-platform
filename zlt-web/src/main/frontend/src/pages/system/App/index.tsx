import { app, deleteApp } from '@/services/system/api';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProFormText, ProTable, QueryFilter } from '@ant-design/pro-components';
import { Typography, message, Popconfirm, Button } from 'antd';
import React, { useRef, useState } from 'react';

const { Link } = Typography;

const handleDelete = async (data: SYSTEM.App) => {
  const hide = message.loading('正在删除');
  try {
    await deleteApp(data.id);
    hide();
    message.success('删除应用成功');
    return true;
  } catch (error) {
    hide();
    message.error('删除应用失败');
    return false;
  }
};

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>();
  const actionRef = useRef<ActionType>();
  const columns: ProColumns<SYSTEM.App>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
      width: 20,
    },
    {
      title: '应用标识',
      dataIndex: 'clientId',
      width: 50,
    },
    {
      title: '应用名称',
      dataIndex: 'clientName',
      width: 50,
    },
    {
      title: '回调地址',
      dataIndex: 'webServerRedirectUri',
      width: 120,
    },
    {
      title: 'oauth授权方式',
      dataIndex: 'authorizedGrantTypes',
      ellipsis: true,
      width: 50,
    },
    {
      title: '自动审批',
      key: 'autoapprove',
      width: 40,
      align: 'center',
      render: (_, record) => (record.autoapprove === 'true' ? '是' : '否'),
    },
    {
      title: '令牌时效',
      dataIndex: 'accessTokenValiditySeconds',
      valueType: 'second',
      width: 40,
    },
    {
      title: '刷新时效',
      dataIndex: 'refreshTokenValiditySeconds',
      valueType: 'second',
      width: 40,
    },
    {
      title: '支持ID令牌',
      key: 'supportIdToken',
      width: 40,
      align: 'center',
      render: (_, record) => (record.supportIdToken ? '是' : '否'),
    },
    {
      title: 'ID时效',
      dataIndex: 'idTokenValiditySeconds',
      valueType: 'second',
      width: 40,
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 40,
      render: (_, entity) => <Popconfirm
      title={`确认删除用户[${entity.clientName}]?`}
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
    </Popconfirm>,
    },
  ];
  return (
    <PageContainer header={{ subTitle: '管理App' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={6}
        className="query-filter"
        onFinish={async (values) => setParams(values)}
        // onReset={() => setParams({})}
      >
        <ProFormText name="searchKey" label="搜索" />
      </QueryFilter>
      <ProTable<SYSTEM.App>
        rowKey="id"
        headerTitle="应用管理"
        actionRef={actionRef}
        request={app}
        columns={columns}
        search={false}
        params={params}
        scroll={{ x: 1300 }}
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
    </PageContainer>
  );
};

export default TableList;
