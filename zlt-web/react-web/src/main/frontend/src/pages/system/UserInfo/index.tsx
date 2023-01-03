import { importImage, saveOrUpdateUser } from '@/services/system/api';
import { UploadOutlined } from '@ant-design/icons';
import type { ProFormInstance } from '@ant-design/pro-components';
import { PageContainer, ProFormText, ProForm, ProFormRadio } from '@ant-design/pro-components';
import { Button, message, Upload } from 'antd';
import type { RcFile } from 'antd/lib/upload';
import React, { useRef, useState } from 'react';
import { useModel } from 'umi';
import styles from './index.less';

const handleEdit = async (fields: SYSTEM.User) => {
  const hide = message.loading('正在更新');
  try {
    const result = await saveOrUpdateUser({ ...fields });
    hide();
    if (result.resp_code === 0) {
      message.success('修改个人信息成功');
      return true;
    } else {
      message.error(result.resp_msg);
      return false;
    }
  } catch (error) {
    hide();
    message.error('修改个人信息失败');
    return false;
  }
};

const handleImport = async (file: RcFile) => {
  const hide = message.loading('正在导入');
  try {
    const formData = new FormData();
    formData.append('file', file);
    const result = await importImage(formData);
    hide();
    message.success('上传头像成功');
    return result;
  } catch (error) {
    hide();
    message.error('上传头像失败');
    return null;
  }
};

const AvatarUpload = ({ avatar, setImage }: { avatar: string, setImage: (headImgUrl: string) => void }) => (
  <>
    <div className={styles.avatar_title}>头像</div>
    <div className={styles.avatar}>
      <img src={avatar} alt="avatar" />
    </div>
    <Upload
      maxCount={1}
      action="/api-file/files-anon"
      showUploadList={false}
      beforeUpload={async (file) => {
        const result = await handleImport(file);
        if (result && result.url) {
          setImage(result.url);
        }
        return false;
      }}
    >
      <div className={styles.button_view}>
        <Button>
          <UploadOutlined />
          更换头像
        </Button>
      </div>
    </Upload>
  </>
);

const UserInfo: React.FC = () => {
  const formRef = useRef<ProFormInstance<SYSTEM.User>>();
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const [user, setUser] = useState<SYSTEM.User>({ ...currentUser });
  const setImage = (headImgUrl: string) => {
    setUser({ ...user, headImgUrl });
  }
  if (!initialState || !currentUser) {
    return null;
  }
  // debugger
  formRef.current?.setFieldsValue(user);
  return (
    <PageContainer header={{ subTitle: '更新个人信息' }}>
      <div className={styles.baseView}>
        <div className={styles.left}>
          <ProForm<SYSTEM.User>
            formRef={formRef}
            //layout="horizontal"
            initialValues={user}
            onFinish={async (values) => {
              const saveUser = { ...user, ...values };
              await handleEdit(saveUser);
              setUser({ ...user });

            }}
          // labelCol={{ span: 8 }}
          // wrapperCol={{ span: 16 }}
          >
            <ProFormText name="username" label="账号" readonly />
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
          </ProForm>
        </div>
        <div className={styles.right}>
          {user.headImgUrl && <AvatarUpload avatar={user.headImgUrl} setImage={setImage} />}
        </div>
      </div>
    </PageContainer>
  );
};

export default UserInfo;
