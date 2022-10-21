import { appAll, deleteMenus, menu, menuOnes, saveOrUpdateMenus } from '@/services/system/api';
import { treeify } from '@/util/treeify';
import { FolderOpenOutlined, MenuOutlined, PlusOutlined, ProfileOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProFormDigit } from '@ant-design/pro-components';
import {
  ProFormRadio,
  ProFormTreeSelect,
  ProForm,
  ProFormSelect,
} from '@ant-design/pro-components';
import {
  PageContainer,
  ProFormText,
  ProTable,
  QueryFilter,
  ModalForm,
} from '@ant-design/pro-components';
import { Button, Space, Tag, Typography, message, Popconfirm } from 'antd';
import React, { useRef, useEffect, useState } from 'react';
import UpdateForm from './components/UpdateForm';


const { Link } = Typography;

const handleAdd = async (fields: SYSTEM.Menu) => {
  const hide = message.loading('正在添加');
  try {
    const result = await saveOrUpdateMenus({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('添加菜单成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('添加菜单失败');
    return false;
  }
};

const handleEdit = async (fields: SYSTEM.Menu) => {
  const hide = message.loading('正在更新');
  try {
    const result = await saveOrUpdateMenus({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('修改菜单成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('修改菜单失败');
    return false;
  }
};

const handleDelete = async (sysUser: SYSTEM.Menu) => {
  const hide = message.loading('正在删除');
  try {
    const result = await deleteMenus(sysUser.id);
    hide();
    if (result.resp_code === 0) {
      message.success('删除菜单成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('删除菜单失败');
    return false;
  }
};
const Generator: React.FC = () => {
  const [tenantId, setTenantId] = useState<string>();
  const [keyword, setKeyword] = useState<string>();
  const [apps, setApps] = useState<SYSTEM.App[]>([]);
  const actionRef = useRef<ActionType>();
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  const [currentRow, setCurrentRow] = useState<SYSTEM.Menu>();
  const [data, setData] = useState<SYSTEM.Menu[]>();
  const [keys, setKeys] = useState<React.Key[]>([]);
  const [expandedRowKeys, setExpandedRowKeys] = useState<React.Key[]>([]);

  const query = async (clientId: string) => {
    const menus = await menu(clientId);
    const treeData = treeify(menus, {});
    menus.forEach((node) => {
      if (node.children && node.children.length === 0) delete node.children;
    });
    setKeys(menus.map((m) => m.id));
    setData(treeData);
  };

  // useEffect(() => {

  //   query({ tenantId: 'webApp' });
  // }, []);

  useEffect(() => {
    (async () => {
      const appData = (await appAll()) || [];
      setApps(appData);
      if (appData.length > 0) {
        const clientId = appData[0].clientId;
        setTenantId(clientId);
        query(clientId);
      }
    })();
  }, []);

  const columns: ProColumns<SYSTEM.Menu>[] = [
    {
      title: '菜单名称',
      key: 'name',
      width: 120,
      onCell: (record) => {
        if (keyword && record.name?.includes(keyword)) return { className: 'hightligh' };
        return {};
      },
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
      onCell: (record) => {
        if (keyword && record.url?.includes(keyword)) return { className: 'hightligh' };
        return {};
      },
    },
    {
      title: '菜单path',
      dataIndex: 'path',
      width: 100,
      ellipsis: true,
      onCell: (record) => {
        if (keyword && record.path?.includes(keyword)) return { className: 'hightligh' };
        return {};
      },
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
          <Link
            onClick={() => {
              setCurrentRow(entity);
              handleUpdateModalVisible(true);
            }}
          >
            修改
          </Link>
          <Popconfirm
            title={`确认删除菜单[${entity.name}]?`}
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
          </Popconfirm>
        </Space>
      ),
    },
  ];
  return (
    <PageContainer header={{ subTitle: '维护系统菜单' }}>
      <QueryFilter
        defaultCollapsed
        split
        span={6}
        className="query-filter"
        onFinish={async (values) => {
          setExpandedRowKeys(keys);
          setKeyword(values.searchValue);
        }}
        // onReset={() => setParams({})}
      >
        <ProFormSelect
          name="tenantId"
          label="所属应用"
          fieldProps={{
            value: tenantId,
            allowClear: false,
            onChange: async (values) => {
              setTenantId(values);
              query(values);
            },
          }}
          options={apps.map((item) => {
            return { label: item.clientId, value: item.clientId };
          })}
        />
        <ProFormText name="searchValue" />
      </QueryFilter>
      <ProTable<SYSTEM.Menu>
        rowKey="id"
        headerTitle="菜单管理"
        actionRef={actionRef}
        request={async () => {
          const menus = await menu(params);
          const treeData = treeify(menus, {});
          menus.forEach((node) => {
            if (node.children && node.children.length === 0) delete node.children;
          });
          setKeys(menus.map((m) => m.id));
          setData(treeData);
        }}
        dataSource={data}
        columns={columns}
        search={false}
        pagination={false}
        expandable={{
          expandedRowKeys,
          onExpandedRowsChange: (expandedRows) => setExpandedRowKeys([...expandedRows]),
        }}
        toolBarRender={() => [
          <>
            <Button
              onClick={() => {
                setExpandedRowKeys(keys);
              }}
            >
              全部展开
            </Button>
            <Button
              onClick={() => {
                setExpandedRowKeys([]);
              }}
            >
              全部折叠
            </Button>
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
        title="添加菜单"
        // layout="horizontal"
        visible={createModalVisible}
        onVisibleChange={handleModalVisible}
        modalProps={{ destroyOnClose: true, okText: '保存' }}
        onFinish={async (values) => {
          const success = await handleAdd(values as SYSTEM.Menu);
          if (success) {
            handleModalVisible(false);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
      >
        <ProForm.Group>
          <ProFormTreeSelect
            label="上级菜单"
            name="parentId"
            width="md"
            request={async () => {
              const menus = await menuOnes();
              if (menus) {
                const a = menus.map((item) => {
                  return { ...item, key: item.id, title: item.name, value: item.id };
                });
                const treeData = treeify(a, {});
                const root = {
                  title: '顶级目录',
                  value: -1,
                  key: -1,
                };
                treeData.unshift(root);
                return treeData;
              }
              return [];
            }}
            fieldProps={{
              fieldNames: {
                label: 'title',
              },
              // treeCheckable: true,
              // showCheckedStrategy: TreeSelect.SHOW_PARENT,
              placeholder: '选择上级菜单名',
            }}
          />
          <ProFormText
            rules={[
              {
                required: true,
                message: '菜单名不为空',
              },
            ]}
            width="md"
            name="name"
            label="菜单名"
            placeholder="输入菜单名"
          />
          <ProFormText width="md" name="url" label="菜单url" placeholder="输入菜单url" />
          <ProFormText width="md" name="path" label="菜单path" placeholder="输入菜单path" />
          <ProFormText
            rules={[
              {
                required: true,
                message: '菜单图标不为空',
              },
            ]}
            width="md"
            name="css"
            label="菜单图标"
            placeholder="输入菜单图标"
          />
          <ProFormRadio.Group
            name="hidden"
            label="是否隐藏"
            width="lg"
            initialValue={false}
            options={[
              {
                label: '否',
                value: false,
              },
              {
                label: '是',
                value: true,
              },
            ]}
            rules={[{ required: true, message: '选择是否隐藏' }]}
          />
          <ProFormRadio.Group
            name="type"
            label="是否为菜单"
            width="lg"
            initialValue={1}
            options={[
              {
                label: '是',
                value: 1,
              },
              {
                label: '否',
                value: 2,
              },
            ]}
            rules={[{ required: true, message: '选择是否为菜单' }]}
          />
          <ProFormSelect
            name="pathMethod"
            label="请求方法"
            width="md"
            valueEnum={{
              GET: 'GET',
              POST: 'POST',
              PUT: 'PUT',
              DELETE: 'DELETE',
            }}
            placeholder="选择请求方法"
          />
          <ProFormDigit
            label="排序号"
            name="sort"
            width="md"
            min={1}
            fieldProps={{ precision: 0 }}
            rules={[{ required: true, message: '选择排序号' }]}
          />
        </ProForm.Group>
      </ModalForm>
      <UpdateForm
        onSubmit={async (values) => {
          if (currentRow) {
            const success = await handleEdit({ ...currentRow, ...values });
            if (success) {
              handleUpdateModalVisible(false);
              if (actionRef.current) {
                actionRef.current.reload();
              }
            }
            setCurrentRow(undefined);
          }
        }}
        onVisibleChange={handleUpdateModalVisible}
        updateModalVisible={updateModalVisible}
        values={currentRow}
      />
    </PageContainer>
  );
};

export default Generator;
