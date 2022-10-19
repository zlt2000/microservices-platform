import { searchUser } from '@/services/system/api';
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
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.username || '' }} />,
    },
    {
      title: '用户名',
      dataIndex: 'nickname',
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.nickname || '' }} />,
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.mobile || '' }} />,
    },
    {
      title: '性别',
      dataIndex: 'sex',
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.sex || '' }} />,
    },
    {
      title: '类别',
      dataIndex: 'type',
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.type || '' }} />,
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
        initialValues={{ searchKey: 'none' }}
        // onFinish={async (values) => setParams(values)}
        onFinish={async (values) => {
          let queryStr = '';
          const { searchKey, searchValue } = values;
          if (searchKey && searchValue) {
            if (searchKey === 'none') {
              queryStr = searchValue;
            } else {
              queryStr = `${searchKey}:${searchValue}`;
            }
          }
          setParams({ ...params, queryStr });
        }}
      >
        <ProFormSelect
          name="searchKey"
          label="搜索"
          valueEnum={{
            none: '全文搜索',
            id: 'ID',
            username: '账号',
            nickname: '用户名',
            mobile: '手机号',
            sex: '性别',
            type: '用户类别',
          }}
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SYSTEM.User>
        rowKey="id"
        headerTitle="用户管理"
        request={searchUser}
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
