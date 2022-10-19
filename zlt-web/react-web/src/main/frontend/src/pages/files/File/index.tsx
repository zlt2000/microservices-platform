import { deleteFile, file } from '@/services/files/api';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import { Typography, message, Popconfirm } from 'antd';
import React, { useRef, useState } from 'react';

const { Link } = Typography;

const handleDelete = async (deletefile: FILES.File) => {
  const hide = message.loading('正在删除');
  try {
    const result = await deleteFile(deletefile.id);
    hide();
    if (result.resp_code === 0) {
      message.success('删除File成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('删除File失败');
    return false;
  }
};

const TableList: React.FC = () => {
  const actionRef = useRef<ActionType>();
  const [params, setParams] = useState<Record<string, string | number>>({ });
  const columns: ProColumns<FILES.File>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
    },
    {
      title: '流媒体名称',
      dataIndex: 'name',
    },
    {
      title: '文件大小（B）',
      dataIndex: 'size',
      valueType: 'digit',
    },
    {
      title: '媒体类型',
      dataIndex: 'contentType',
    },
    {
      title: '文件系统',
      dataIndex: 'grantType',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
    },
    {
      title: '内容',
      dataIndex: 'accountType',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, entity) => <Popconfirm
        title={`确认删除文件[${entity.name}]?`}
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
    <PageContainer header={{ subTitle: '管理文件' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={6}
        className="query-filter"
        onFinish={async (values) => setParams(values)}
      >
        <ProFormText name="name" label="搜索" placeholder="输入关键字" />
      </QueryFilter>
      <ProTable<FILES.File>
        rowKey="id"
        headerTitle="文件管理"
        request={file}
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
