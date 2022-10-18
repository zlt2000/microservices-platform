import { app, saveOrUpdateApp, deleteApp } from '@/services/system/api';
import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProFormText, ProTable, QueryFilter } from '@ant-design/pro-components';
import { Typography, message, Popconfirm, Button, Space } from 'antd';
import React, { useRef, useState } from 'react';
import CreateForm from './components/CreateForm';
import UpdateForm from './components/UpdateForm';

const { Link } = Typography;

const handleAdd = async (fields: SYSTEM.App) => {
  const hide = message.loading('正在添加');
  try {
    const result = await saveOrUpdateApp({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('添加应用成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('添加应用失败');
    return false;
  }
};

const handleEdit = async (fields: SYSTEM.App) => {
  const hide = message.loading('正在更新');
  try {
    const result = await saveOrUpdateApp({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('修改应用成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('修改应用失败');
    return false;
  }
};

const handleDelete = async (data: SYSTEM.App) => {
  if (!data.id) {
    message.error('缺少ID信息，删除应用失败');
    return false;
  }
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
  const [createModalVisible, handleCreateModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<SYSTEM.App>();
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
      renderText: (_, record) => (record.autoapprove === 'true' ? '是' : '否'),
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
      renderText: (_, record) => (record.supportIdToken ? '是' : '否'),
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
          <Popconfirm
            title={`确认删除应用[${entity.clientId}]?`}
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
                handleCreateModalVisible(true);
              }}
            >
              <PlusOutlined /> 添加
            </Button>
          </>,
        ]}
      />
      <CreateForm
        visible={createModalVisible}
        onVisibleChange={handleCreateModalVisible}
        onSubmit={async (values) => {
          const authorizedGrantTypeCodes: string[] = values.authorizedGrantTypeCodes || [];
          const authorizedGrantTypes = authorizedGrantTypeCodes.join(',');
          delete values.authorizedGrantTypeCodes;
          const success = await handleAdd({ ...values, authorizedGrantTypes });
          if (success) {
            handleCreateModalVisible(false);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
      />
      <UpdateForm
        onSubmit={async (values) => {
          if (currentRow) {
            const authorizedGrantTypeCodes: string[] = values.authorizedGrantTypeCodes || [];
            const authorizedGrantTypes = authorizedGrantTypeCodes.join(',');
            delete values.authorizedGrantTypeCodes;
            const success = await handleEdit({ ...currentRow, ...values, authorizedGrantTypes });
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
        visible={updateModalVisible}
        values={currentRow}
      />
    </PageContainer>
  );
};

export default TableList;
