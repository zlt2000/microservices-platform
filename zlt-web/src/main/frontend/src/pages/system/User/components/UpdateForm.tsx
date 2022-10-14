import { role } from '@/services/system/api';
import {
  ModalForm,
  ProForm,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
} from '@ant-design/pro-components';
import { Form } from 'antd';
import React, { useEffect } from 'react';

export type FormValueType = {
  roleIds?: number[];
} & Partial<SYSTEM.User>;

export type UpdateFormProps = {
  onVisibleChange: (flag: boolean) => void;
  onSubmit: (values: FormValueType) => Promise<void>;
  updateModalVisible: boolean;
  values?: SYSTEM.User;
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [form] = Form.useForm();
  const { values } = props;
  useEffect(() => {
    if (values?.roles) {
      const roleIds = values.roles.map((r) => r.id);
      const formData: FormValueType = { ...values, roleIds };
      form.setFieldsValue(formData);
    }
  }, [form, values]);
  return (
    <ModalForm
      title="修改用户"
      form={form}
      // layout="horizontal"
      visible={props.updateModalVisible}
      // initialValues={formData}
      onFinish={props.onSubmit}
      onVisibleChange={props.onVisibleChange}
    >
      <ProForm.Group>
        <ProFormText
          rules={[
            {
              required: true,
              message: '账号不为空',
            },
          ]}
          width="md"
          name="username"
          label="账号"
          placeholder="输入账号"
        />
        <ProFormText
          rules={[
            {
              required: true,
              message: '用户名不为空',
            },
          ]}
          width="md"
          name="nickname"
          label="用户名"
          placeholder="输入用户名"
        />
        <ProFormText
          rules={[
            {
              required: true,
              message: '手机号不为空',
            },
          ]}
          width="md"
          name="mobile"
          label="手机号"
          placeholder="输入手机号"
        />
        <ProFormRadio.Group
          name="sex"
          label="性别"
          width="md"
          options={[
            {
              label: '男',
              value: 0,
            },
            {
              label: '女',
              value: 1,
            },
          ]}
        />
        <ProFormSelect
          mode="multiple"
          name="roleIds"
          label="角色"
          width="md"
          request={async () => {
            const roles = await role();
            if (roles)
              return roles.map((r) => {
                return { label: r.name, value: r.id };
              });
            return [];
          }}
          placeholder="选择角色"
        />
      </ProForm.Group>
    </ModalForm>
  );
};

export default UpdateForm;
