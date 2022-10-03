import { token } from '@/services/system/api';
import type { ProColumns } from '@ant-design/pro-components';
import {
  ProFormSelect,
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import { Typography, message } from 'antd';
import React, { useState } from 'react';

const { Link } = Typography;

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({ tenantId: 'webApp' });

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
      render: () => <Link onClick={() => message.info('演示环境不支持该功能')}>删除</Link>,
    },
  ];

  return (
    <PageContainer header={{ subTitle: '管理会话Token' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={6}
        className="query-filter"
        initialValues={params}
        onFinish={async (values) => setParams(values)}
        // onReset={() => setParams({})}
      >
        <ProFormSelect
          name="tenantId"
          label="所属应用"
          valueEnum={{
            webApp: 'pc端',
            app: '移动端',
            zlt: '第三方应用',
          }}
          allowClear={false}
        />
        <ProFormText name="username" label="搜索" placeholder="输入用户名" />
      </QueryFilter>
      <ProTable<SYSTEM.Token>
        rowKey="tokenValue"
        headerTitle="Token管理"
        request={token}
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
