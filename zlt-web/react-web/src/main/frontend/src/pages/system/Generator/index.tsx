import { exportGenerator, generator } from '@/services/system/api';
import type { ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProFormText, ProTable, QueryFilter } from '@ant-design/pro-components';
import { Typography } from 'antd';
import React, { useState } from 'react';

const { Link } = Typography;

const Generator: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>();

  const columns: ProColumns<SYSTEM.Generator>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
      width: 20,
    },
    {
      title: '数据库引擎',
      dataIndex: 'engine',
      width: 50,
    },
    {
      title: '表名',
      dataIndex: 'tableName',
      width: 50,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      width: 100,
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 40,
      render: (_, entity) => (
        <Link
          onClick={() => {
            if (entity.tableName) exportGenerator(entity.tableName);
          }}
        >
          生成
        </Link>
      ),
    },
  ];
  return (
    <PageContainer header={{ subTitle: '生成代码' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={6}
        className="query-filter"
        onFinish={async (values) => setParams(values)}
        // onReset={() => setParams({})}
      >
        <ProFormText name="tableName" label="搜索" placeholder="输入表名" />
      </QueryFilter>
      <ProTable<SYSTEM.Generator>
        headerTitle="代码生成器"
        request={generator}
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

export default Generator;
