import { indice } from '@/services/search/api';
import { PlusOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  ProForm,
  ProFormTextArea,
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
} from '@ant-design/pro-components';
import { Typography, message, Space, Button } from 'antd';
import React, { useState } from 'react';
import IndexView from './components/IndexView';

const { Link } = Typography;

type IndexViewProps = {
  indexName?: string;
  open: boolean;
};

const TableList: React.FC = () => {
  const [params, setParams] = useState<Record<string, string | number>>();
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [indexView, setIndexView] = useState<IndexViewProps>({ open: false });

  const columns: ProColumns<SEARCH.Indice>[] = [
    {
      dataIndex: 'index',
      valueType: 'indexBorder',
    },
    {
      title: '索引健康',
      dataIndex: 'health',
    },
    {
      title: '索引状态',
      dataIndex: 'status',
    },
    {
      title: '索引名',
      dataIndex: 'index',
    },
    {
      title: '文档数',
      dataIndex: 'docsCount',
    },
    {
      title: '文档删除数',
      dataIndex: 'docsDeleted',
    },
    {
      title: '索引大小',
      dataIndex: 'storeSize',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Link
            onClick={() => {
              setIndexView({ indexName: record.index, open: true });
            }}
          >
            查看
          </Link>
          <Link onClick={() => message.info('演示环境不支持该功能')}>删除</Link>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer header={{ subTitle: '索引管理' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={12}
        className="query-filter"
        onFinish={async (values) => setParams(values)}
        // onReset={() => setParams({})}
      >
        <ProFormText
          name="queryStr"
          label="索引名"
          placeholder="输入关键字，模糊搜索需要加通配符*"
        />
      </QueryFilter>
      <ProTable<SEARCH.Indice>
        rowKey="index"
        headerTitle="索引管理"
        request={indice}
        columns={columns}
        search={false}
        params={params}
        pagination={false}
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
      <ModalForm
        title="新增索引"
        // layout="horizontal"
        width={800}
        visible={createModalVisible}
        onVisibleChange={handleModalVisible}
        onFinish={async () => {
          message.info('演示环境不支持该功能');
        }}
      >
        <ProForm.Group>
          <ProFormText
            rules={[
              {
                required: true,
                message: '索引名不为空',
              },
            ]}
            width="md"
            name="index"
            label="索引名"
            placeholder="输入索引名"
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: '分片数不为空',
              },
            ]}
            width="md"
            name="numberOfShards"
            label="分片数"
            placeholder="输入分片数"
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: '副本数不为空',
              },
            ]}
            width="md"
            name="numberOfReplicas"
            label="副本数"
            placeholder="输入副本数"
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: '类型不为空',
              },
            ]}
            width="md"
            name="type"
            label="类型"
            placeholder="输入类型"
          />
          <ProFormTextArea
            rules={[
              {
                required: true,
                message: 'mappings不为空',
              },
            ]}
            width="xl"
            name="mappingsSource"
            label="mappings"
            placeholder="输入mappings的JSON内容"
          />
        </ProForm.Group>
      </ModalForm>
      <IndexView {...indexView} onCancel={() => setIndexView({ open: false })} />
    </PageContainer>
  );
};

export default TableList;
