import { menuForAuth } from '@/services/system/api';
import { treeify } from '@/util/treeify';
import type { TreeDataNode } from 'antd';
import { Modal, Tree, message } from 'antd';
import React, { useEffect, useState } from 'react';

type AssignAuthProps = {
  roleId?: number;
  tenantId?: string;
  onCancel: () => void;
  assignModalVisible: boolean;
};

type Key = string | number;

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
      onOk={() => {
        message.info('演示环境不支持该功能');
        props.onCancel();
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
