import { menuOnes } from '@/services/system/api';
import {
  ModalForm,
  ProForm,
  ProFormDigit,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
  ProFormTreeSelect,
} from '@ant-design/pro-components';
import { treeify } from '@/util/treeify';
import { Form } from 'antd';
import React, { useEffect } from 'react';

export type FormValueType = {
  roleIds?: number[];
} & Partial<SYSTEM.User>;

export type UpdateFormProps = {
  onVisibleChange: (flag: boolean) => void;
  onSubmit: (values: FormValueType) => Promise<void>;
  updateModalVisible: boolean;
  values?: SYSTEM.Menu;
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [form] = Form.useForm();
  const { values } = props;

  useEffect(() => {
    form.setFieldsValue(values);
  }, [form, values]);

  return (
    <ModalForm
      title="修改菜单"
      form={form}
      // layout="horizontal"
      visible={props.updateModalVisible}
      // initialValues={formData}
      onFinish={props.onSubmit}
      onVisibleChange={props.onVisibleChange}
    >
      <ProForm.Group>
        <ProFormTreeSelect
          label="上级菜单"
          name="parentId"
          width="md"
          request={async () => {
            const menus = await menuOnes();
            if (menus) {
              let a = menus.map(item => {
                return { ...item, key: item.id, title: item.name, value: item.id }
              })
              let treeData = treeify(a, {});
              let root = {
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
        <ProFormText
          width="md"
          name="url"
          label="菜单url"
          placeholder="输入菜单url"
        />
        <ProFormText
          width="md"
          name="path"
          label="菜单path"
          placeholder="输入菜单path"
        />
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
  );
};

export default UpdateForm;
