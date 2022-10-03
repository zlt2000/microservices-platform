import { user } from '@/services/system/api';
import type { ProColumns } from '@ant-design/pro-components';
import {
  ProFormSelect,
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import React, { useState } from 'react';

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({});

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
      title: '类别',
      dataIndex: 'type',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
  ];

  return (
    <PageContainer header={{ subTitle: '查询系统中的用户账号' }}>
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
      />
    </PageContainer>
  );
};

export default TableList;
