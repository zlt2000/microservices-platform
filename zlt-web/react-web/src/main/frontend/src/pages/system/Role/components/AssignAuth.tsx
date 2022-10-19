import { assignMenu, menuForAuth } from '@/services/system/api';
import { treeify } from '@/util/treeify';
import type { TreeDataNode } from 'antd';
import { Modal, Tree, message } from 'antd';
import React, { useEffect, useState } from 'react';

type AssignAuthProps = {
  roleId: number;
  tenantId?: string;
  onCancel: () => void;
  assignModalVisible: boolean;
};

type Key = string | number;

const handleAssign = async (roleId: number, menuIds: Key[]) => {
  const hide = message.loading('正在添加');
  try {
    const result = await assignMenu({ roleId, menuIds });
    hide();
    if (result.resp_code === 0) {
      message.success('权限分配成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('权限分配失败');
    return false;
  }
};

const AssignAuth: React.FC<AssignAuthProps> = (props) => {
  const [treeData, setTreeData] = useState<TreeDataNode[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<Key[]>([]);
  const { roleId, tenantId } = props;
  useEffect(() => {
    (async () => {
      if (roleId && tenantId) {
        const menus = await menuForAuth(roleId, tenantId);
        const arr: (SYSTEM.Menu & TreeDataNode)[] = menus.map((m) => {
          return { ...m, title: m.name, key: m.id };
        });
        // console.log(menus);
        const tree = treeify(arr, { parentId: 'pId' });
        const keys = menus.filter((m) => m.checked).map((m) => m.id);
        setTreeData(tree);
        setCheckedKeys(keys);
      }
    })();
  }, [roleId, tenantId]);

  return (
    <Modal
      title="权限分配"
      // layout="horizontal"
      open={props.assignModalVisible}
      // initialValues={formData}
      destroyOnClose
      onOk={async () => {
        const success = await handleAssign(roleId, checkedKeys);
        if (success) {
          setTreeData([]);
          setCheckedKeys([]);
          props.onCancel();;
        }
      }}
      onCancel={() => {
        setTreeData([]);
        setCheckedKeys([]);
        props.onCancel();
      }}
    >
      <Tree
        defaultExpandAll
        checkable
        treeData={treeData}
        checkedKeys={checkedKeys}
        onCheck={(keys) => {
          setCheckedKeys(Array.isArray(keys) ? keys : keys.checked);
        }}
      />
    </Modal>
  );
};

export default AssignAuth;
