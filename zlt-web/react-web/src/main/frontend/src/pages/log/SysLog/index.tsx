import { syslog } from '@/services/search/api';
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
import TraceView from './components/TraceView';
import { Typography } from 'antd';

type TraceViewProps = {
  traceId?: string;
  open: boolean;
};

const { Link } = Typography;

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>({
    sortCol: 'timestamp',
    isHighlighter: 'true',
  });
  const [traceView, setTraceView] = useState<TraceViewProps>({ open: false });

  const columns: ProColumns<SEARCH.SysLog>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
      width: 20,
    },
    {
      title: '时间',
      key: 'timestamp',
      width: 75,
      render(_, entity) {
        return moment(entity.timestamp).format('YYYY-MM-DD HH:mm:SS');
      },
    },
    {
      title: '日志信息',
      dataIndex: 'message',
      ellipsis: true,
      width: 200,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.message || '' }} />,
    },
    {
      title: '日志级别',
      dataIndex: 'logLevel',
      width: 40,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.logLevel || '' }} />,
    },
    {
      title: '应用名',
      dataIndex: 'appName',
      width: 50,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.appName || '' }} />,
    },
    {
      title: '追踪id',
      key: 'traceId',
      width: 80,
      render(_, entity) {
        return (
          <Link
            onClick={() => {
              setTraceView({ traceId: entity.traceId.replace('<mark>', '').replace('</mark>', ''), open: true });
            }}
          >
            <span dangerouslySetInnerHTML={{ __html: entity.traceId || '' }} />
          </Link>
        );
      },
    },
    {
      title: 'spanId',
      dataIndex: 'spanId',
      width: 40,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.spanId || '' }} />,
    },
    {
      title: '类名',
      dataIndex: 'classname',
      width: 110,
      ellipsis: true,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.classname || '' }} />,
    },
    {
      title: '线程名',
      dataIndex: 'threadName',
      width: 50,
      ellipsis: true,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.threadName || '' }} />,
    },
    {
      title: '服务ip',
      dataIndex: 'serverIp',
      width: 50,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.serverIp || '' }} />,
    },
    {
      title: '服务端口',
      dataIndex: 'serverPort',
      width: 35,
      render: (_, entity) => <span dangerouslySetInnerHTML={{ __html: entity.serverPort || '' }} />,
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
            message: '日志信息',
            logLevel: '日志级别',
            appName: '应用名',
            classname: '类别',
            traceId: '链路追踪ID',
          }}
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SEARCH.SysLog>
        rowKey="id"
        headerTitle="系统日志"
        request={syslog}
        columns={columns}
        search={false}
        params={params}
        size="small"
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        scroll={{ x: 1600 }}
      />
      <TraceView {...traceView} onCancel={() => setTraceView({ open: false })} />
    </PageContainer>
  );
};

export default TableList;
