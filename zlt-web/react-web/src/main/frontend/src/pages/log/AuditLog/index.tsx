import { auditlog } from '@/services/search/api';
import type { ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
  ProFormSelect,
} from '@ant-design/pro-components';
import React, { useState } from 'react';
import moment from 'moment';

const AuditLog: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>();

  const columns: ProColumns<SEARCH.AuditLog>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
      width: 20,
    },
    {
      title: '日志时间',
      key: 'timestamp',
      width: 75,
      render(_, entity) {
        return moment(entity.timestamp).format('YYYY-MM-DD HH:mm:SS');
      },
    },
    {
      title: '用户id',
      dataIndex: 'userId',
      width: 30,
    },
    {
      title: '用户名',
      dataIndex: 'userName',
      width: 40,
    },
    {
      title: '租户id',
      dataIndex: 'clientId',
      width: 50,
    },
    {
      title: '操作信息',
      dataIndex: 'operation',
      width: 150,
      ellipsis: true,
      render: (_, entity) =>
        entity.operation && <span dangerouslySetInnerHTML={{ __html: entity.operation }} />,
    },
    {
      title: '应用名',
      dataIndex: 'appName',
      width: 50,
      ellipsis: true,
    },
    {
      title: '类名',
      dataIndex: 'className',
      width: 150,
      ellipsis: true,
    },
  ];

  return (
    <PageContainer header={{ subTitle: '查看系统日志' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={8}
        className="query-filter"
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
        // onReset={() => setParams({})}
      >
        <ProFormSelect
          name="searchKey"
          label="搜索"
          valueEnum={{
            none: '全文搜索',
            appName: '应用名',
            classname: '类别',
            methodName: '方法名',
            userId: '用户ID',
            userName: '用户名',
            clientId: '租户ID',
            operation: '操作信息',
          }}
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SEARCH.AuditLog>
        className="audit-log"
        rowKey="id"
        headerTitle="系统日志"
        request={auditlog}
        columns={columns}
        search={false}
        params={params}
        size="small"
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
        }}
      />
    </PageContainer>
  );
};

export default AuditLog;
