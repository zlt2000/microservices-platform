import { PageContainer, StatisticCard } from '@ant-design/pro-components';
import { Card, Tag, Space, Tabs, Col, Row } from 'antd';
import RcResizeObserver from 'rc-resize-observer';
import React, { useState, useEffect } from 'react';
import { statistic } from '@/services/welcome/api';
import { Area, Pie } from '@ant-design/plots';
import styles from './Welcome.less';

const { Divider } = StatisticCard;

const Welcome: React.FC = () => {
  const [responsive, setResponsive] = useState<boolean>(false);
  const [statistics, setStatistics] = useState<WELCOME.Statistic>({});
  const [weeks, setWeeks] = useState<WELCOME.AreaData[]>([]);
  const [days, setDays] = useState<WELCOME.AreaData[]>([]);

  const mapWeek = (result: WELCOME.Statistic) => {
    const { statWeek_items, statWeek_pv, statWeek_uv } = result;
    if (statWeek_items && statWeek_pv && statWeek_uv) {
      const weekData = statWeek_pv
        .map((pv, index) => {
          return { type: '访问量(PV)', date: statWeek_items[index], value: pv };
        })
        .concat(
          statWeek_uv.map((uv, index) => {
            return { type: '独立用户(UV)', date: statWeek_items[index], value: uv };
          }),
        );
      setWeeks(weekData);
    }
  };

  const mapDay = (result: WELCOME.Statistic) => {
    const { statDate_items, statDate_pv, statDate_uv } = result;
    if (statDate_items && statDate_pv && statDate_uv) {
      const dayData = statDate_pv
        .map((pv, index) => {
          return { type: '访问量(PV)', date: statDate_items[index], value: pv };
        })
        .concat(
          statDate_uv.map((uv, index) => {
            return { type: '独立用户(UV)', date: statDate_items[index], value: uv };
          }),
        );
      setDays(dayData);
    }
  };

  useEffect(() => {
    (async () => {
      const result = await statistic();
      if (result) {
        setStatistics(result);
        mapWeek(result);
        mapDay(result);
      }
    })();
  }, []);

  return (
    <PageContainer
      subTitle="前后端分离的微服务架构"
      tags={
        <>
          <Tag color="blue">简单</Tag>
          <Tag color="blue">高效</Tag>
        </>
      }
    >
      <RcResizeObserver
        key="resize-observer"
        onResize={(offset) => {
          setResponsive(offset.width < 596);
        }}
      >
        <StatisticCard.Group direction={responsive ? 'column' : 'row'}>
          <StatisticCard
            statistic={{
              title: (
                <Space size="small">
                  在线人数<Tag color="#434343">时</Tag>
                </Space>
              ),
              value: statistics.currHour_uv,
            }}
          />
          <Divider type={responsive ? 'horizontal' : 'vertical'} />
          <StatisticCard
            statistic={{
              title: (
                <Space size="small">
                  PV<Tag color="#52c41a">天</Tag>
                </Space>
              ),
              value: statistics.currDate_pv,
            }}
          />
          <Divider type={responsive ? 'horizontal' : 'vertical'} />
          <StatisticCard
            statistic={{
              title: (
                <Space size="small">
                  UV<Tag color="#52c41a">天</Tag>
                </Space>
              ),
              value: statistics.currDate_uv,
            }}
          />
          <Divider type={responsive ? 'horizontal' : 'vertical'} />
          <StatisticCard
            statistic={{
              title: (
                <Space size="small">
                  周访问量<Tag color="#1890ff">周</Tag>
                </Space>
              ),
              value: statistics.currWeek_pv,
            }}
          />
          <Divider type={responsive ? 'horizontal' : 'vertical'} />
          <StatisticCard
            statistic={{
              title: (
                <Space size="small">
                  月访问量<Tag color="#faad14">月</Tag>
                </Space>
              ),
              value: statistics.currMonth_pv,
            }}
          />
        </StatisticCard.Group>
      </RcResizeObserver>
      <Tabs defaultActiveKey="1" className={styles.stat}>
        <Tabs.TabPane tab="周流量趋势" key="1">
          <Area
            data={weeks}
            xField="date"
            yField="value"
            seriesField="type"
            smooth
            height={200}
            legend={{
              layout: 'horizontal',
              position: 'top',
            }}
          />
        </Tabs.TabPane>
        <Tabs.TabPane tab="天流量趋势" key="2">
          <Area
            data={days}
            xField="date"
            yField="value"
            seriesField="type"
            smooth
            height={200}
            legend={{
              layout: 'horizontal',
              position: 'top',
            }}
          />
        </Tabs.TabPane>
      </Tabs>
      <Row gutter={16} className={styles.pie}>
        <Col span={12}>
          <Card
            title="浏览器分布"
            bordered={false}
            size="small"
            headStyle={{ textAlign: 'center' }}
          >
            <Pie
              height={240}
              data={statistics.browser_datas ?? []}
              angleField="value"
              colorField="name"
              appendPadding={10}
              radius={0.75}
              label={{
                type: 'spider',
                labelHeight: 28,
                content: '{name}\n{percentage}',
              }}
              interactions={[
                {
                  type: 'element-selected',
                },
                {
                  type: 'element-active',
                },
              ]}
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card
            title="操作系统分布"
            bordered={false}
            size="small"
            headStyle={{ textAlign: 'center' }}
          >
            <Pie
              height={240}
              data={statistics.operatingSystem_datas ?? []}
              angleField="value"
              colorField="name"
              appendPadding={10}
              radius={0.75}
              label={{
                type: 'spider',
                labelHeight: 28,
                content: '{name}\n{percentage}',
              }}
              interactions={[
                {
                  type: 'element-selected',
                },
                {
                  type: 'element-active',
                },
              ]}
            />
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Welcome;
