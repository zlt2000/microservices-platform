import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import { useIntl } from 'umi';

const Footer: React.FC = () => {
  const intl = useIntl();
  const defaultMessage = intl.formatMessage({
    id: 'app.copyright.produced',
    defaultMessage: 'zlt版权所有：https://gitee.com/zlt2000/microservices-platform',
  });

  // const currentYear = new Date().getFullYear();

  return (
    <DefaultFooter
      copyright={`${2018} ${defaultMessage}`}
      links={[
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/zlt2000/microservices-platform',
          blankTarget: true,
        },
        {
          key: 'beian',
          title: '粤ICP备19081412号',
          href: 'https://beian.miit.gov.cn/',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
