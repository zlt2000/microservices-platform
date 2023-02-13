import {
  ModalForm,
  ProForm, ProFormRadio,
  ProFormText,
} from '@ant-design/pro-components';
import { Form } from 'antd';
import React, { useEffect } from 'react';

export type FormValueType = Partial<SYSTEM.Role>;

export type UpdateFormProps = {
  onVisibleChange: (flag: boolean) => void;
  onSubmit: (values: FormValueType) => Promise<void>;
  updateModalVisible: boolean;
  values?: SYSTEM.Role;
};

const UpdateForm: React.FC<UpdateFormProps> = (props) => {
  const [form] = Form.useForm();
  const { values } = props;

  useEffect(() => {
    form.setFieldsValue(values);
  }, [form, values]);

  return (
    <ModalForm
      title="修改角色"
      form={form}
      width={500}
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
              message: '角色名不为空',
            },
          ]}
          width="lg"
          name="name"
          label="角色名"
          placeholder="输入角色名"
        />
        <ProFormText
          rules={[
            {
              required: true,
              message: 'Code不为空',
            },
          ]}
          width="lg"
          name="code"
          label="Code"
          placeholder="输入Code"
        />
        <ProFormRadio.Group
          name="dataScope"
          label="数据权限"
          width="lg"
          options={[
            {
              label: '全部',
              value: 'ALL',
            },
            {
              label: '本人',
              value: 'CREATOR',
            },
          ]}
        />
      </ProForm.Group>
    </ModalForm>
  );
};

export default UpdateForm;
