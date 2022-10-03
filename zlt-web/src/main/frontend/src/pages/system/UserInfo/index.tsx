import { currentUser2 } from '@/services/login/api';
import { UploadOutlined } from '@ant-design/icons';
import type { ProFormInstance } from '@ant-design/pro-components';
import { PageContainer, ProFormText, ProForm, ProFormRadio } from '@ant-design/pro-components';
import { Button, Upload } from 'antd';
import React, { useRef } from 'react';
import { useModel } from 'umi';
import styles from './index.less';

const AvatarView = ({ avatar }: { avatar: string }) => (
  <>
    <div className={styles.avatar_title}>头像</div>
    <div className={styles.avatar}>
      <img src={avatar} alt="avatar" />
    </div>
    <Upload showUploadList={false}>
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
  const formRef = useRef<ProFormInstance<API.CurrentUser>>();
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState;

  return (
    <PageContainer header={{ subTitle: '更新个人信息' }}>
      <div className={styles.baseView}>
        <div className={styles.left}>
          <ProForm<API.CurrentUser>
            formRef={formRef}
            //layout="horizontal"
            request={currentUser2}
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
          <AvatarView avatar={currentUser.headImgUrl} />
        </div>
      </div>
    </PageContainer>
  );
};

export default UserInfo;
