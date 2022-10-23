import { Button, Modal } from 'antd';
import React, { useEffect, useState } from 'react';
import { JSONTree } from 'react-json-tree';
import { index } from '@/services/search/api';
import styles from '../index.less';
import { NodeIndexOutlined } from '@ant-design/icons';

type IndexViewProps = {
  indexName?: string;
  onCancel: () => void;
  open: boolean;
};

const IndexView: React.FC<IndexViewProps> = (props) => {
  const [json, setJson] = useState<any>({});
  const { indexName } = props;
  useEffect(() => {
    (async () => {
      if (indexName) {
        const data = await index(indexName);
        console.log(data);
        setJson(data);
      }
    })();
  }, [indexName]);

  return (
    <Modal
      title={
        <>
          <NodeIndexOutlined />
          查看索引
        </>
      }
      className={styles.viewModal}
      width={800}
      open={props.open}
      onCancel={props.onCancel}
      footer={
        <Button type="primary" onClick={props.onCancel}>
          关闭
        </Button>
      }
    >
      <div className={styles.view}>
        <JSONTree
          data={json}
          hideRoot
          shouldExpandNode={(a, b, level) => level < 3}
          invertTheme={false}
        />
      </div>
    </Modal>
  );
};

export default IndexView;
