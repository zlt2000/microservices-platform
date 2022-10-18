import { appAll, deleteToken, token } from '@/services/system/api';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ProFormSelect,
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import { Typography, message, Popconfirm, Form } from 'antd';
import React, { useEffect, useRef, useState } from 'react';

const { Link } = Typography;

const handleDelete = async (delToken: SYSTEM.Token) => {
  const hide = message.loading('正在删除');
  try {
    const result = await deleteToken(delToken.tokenValue);
    hide();
    if (result.resp_code === 0) {
      message.success('删除Token成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('删除Token失败');
    return false;
  }
};

const TableList: React.FC = () => {
  const [form] = Form.useForm();
  const actionRef = useRef<ActionType>();
  const [apps, setApps] = useState<SYSTEM.App[]>([]);
  const [params, setParams] = useState<Record<string, string | number>>({});
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

  const columns: ProColumns<SYSTEM.Token>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
    },
    {
      title: 'token',
      dataIndex: 'tokenValue',
    },
    {
      title: '到期时间',
      dataIndex: 'expiration',
      valueType: 'dateTime',
    },
    {
      title: '用户名',
      dataIndex: 'username',
    },
    {
      title: '授权类型',
      dataIndex: 'grantType',
    },
    {
      title: '所属应用',
      dataIndex: 'clientId',
    },
    {
      title: '账号类型',
      dataIndex: 'accountType',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, entity) => <Popconfirm
        title={`确认删除Token?`}
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
    <PageContainer header={{ subTitle: '管理会话Token' }}>
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
          fieldProps={{
            value: params.tenantId,
            allowClear: false,
            onChange:async (values) => setParams({tenantId: values})
          }}
          options={apps.map((item) => {
            return { label: item.clientId, value: item.clientId };
          })}
        />
        <ProFormText name="username" label="搜索" placeholder="输入用户名" />
      </QueryFilter>
      <ProTable<SYSTEM.Token>
        rowKey="tokenValue"
        headerTitle="Token管理"
        request={token}
        actionRef={actionRef}
        columns={columns}
        search={false}
        params={params}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
        }}
      />
    </PageContainer>
  );
};

export default TableList;
