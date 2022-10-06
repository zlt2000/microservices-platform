import { menu } from '@/services/system/api';
import { treeify } from '@/util/treeify';
import { FolderOpenOutlined, MenuOutlined, ProfileOutlined } from '@ant-design/icons';
import type { ProColumns } from '@ant-design/pro-components';
import { ProFormSelect } from '@ant-design/pro-components';
import { PageContainer, ProFormText, ProTable, QueryFilter } from '@ant-design/pro-components';
import { Space, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';

const { Link } = Typography;

const Generator: React.FC = () => {
  const [data, setData] = useState<SYSTEM.Menu[]>();

  const query = async (params: Record<string, string | number>) => {
    const menus = (await menu(params)) ?? [];
    const treeData = treeify(menus, {});
    menus.forEach((node) => {
      if (node.children && node.children.length === 0) delete node.children;
    });
    // debugger;
    setData(treeData);
  };

  useEffect(() => {
    query({ tenantId: 'webApp' });
  }, []);

  const columns: ProColumns<SYSTEM.Menu>[] = [
    {
      title: '菜单名称',
      key: 'name',
      width: 80,
      render(dom, entity) {
        if (entity.type === 1) {
          if (entity.url?.startsWith('javascript')) {
            return (
              <Space size="small">
                <FolderOpenOutlined />
                {entity.name}
              </Space>
            );
          } else {
            return (
              <Space size="small">
                <MenuOutlined />
                {entity.name}
              </Space>
            );
          }
        } else {
          return (
            <Space size="small">
              <ProfileOutlined />
              {entity.name}
            </Space>
          );
        }
      },
    },
    {
      title: '菜单url',
      dataIndex: 'url',
      width: 100,
    },
    {
      title: '菜单path',
      dataIndex: 'path',
      width: 100,
      ellipsis: true,
    },
    {
      title: '样式',
      dataIndex: 'css',
      width: 80,
    },
    {
      title: '排序号',
      dataIndex: 'sort',
      width: 50,
    },
    {
      title: '类型',
      key: 'type',
      width: 50,
      render(_, entity) {
        if (entity.type === 1) {
          if (entity.url?.startsWith('javascript')) {
            return <Tag color="green">目录</Tag>;
          } else {
            return <Tag color="blue">菜单</Tag>;
          }
        } else {
          return <Tag color="cyan">资源</Tag>;
        }
      },
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right',
      width: 80,
      render: (_, entity) => (
        <Space>
          <Link onClick={() => {}}>修改</Link>
          <Link onClick={() => {}}>删除</Link>
        </Space>
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
        onFinish={async (values) => query(values)}
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
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SYSTEM.Menu>
        rowKey="id"
        headerTitle="菜单管理"
        dataSource={data}
        columns={columns}
        search={false}
        pagination={false}
      />
    </PageContainer>
  );
};

export default Generator;
