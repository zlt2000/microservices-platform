import { Button, Modal, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import { tracelog } from '@/services/search/api';
import styles from '../index.less';
import type { TableColumnType } from 'antd';
import { treeify } from 'treeify-js';
import { NodeIndexOutlined } from '@ant-design/icons';

type IndexViewProps = {
  traceId?: string;
  onCancel: () => void;
  open: boolean;
};

const TraceView: React.FC<IndexViewProps> = (props) => {
  const [traces, setTraces] = useState<SEARCH.TraceLog[]>();
  const { traceId } = props;

  const columns: TableColumnType<SEARCH.TraceLog>[] = [
    {
      title: '应用名',
      dataIndex: 'appName',
    },
    {
      title: 'spanId',
      dataIndex: 'spanId',
    },
    {
      title: '服务ip',
      dataIndex: 'serverIp',
    },
    {
      title: '服务端口',
      dataIndex: 'serverPort',
    },
  ];

  useEffect(() => {
    (async () => {
      if (traceId) {
        const data = (await tracelog(traceId)) ?? [];
        const treeData = treeify(data, { id: 'spanId', root: '-1', multi: true });
        setTraces(treeData);
      }
    })();
  }, [traceId]);

  return (
    <Modal
      title={
        <>
          <NodeIndexOutlined />
          日志链路
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
      <Table<SEARCH.TraceLog>
        columns={columns}
        dataSource={traces}
        pagination={false}
        size="small"
      />
    </Modal>
  );
};

export default TraceView;
