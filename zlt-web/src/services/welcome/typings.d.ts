// @ts-ignore
/* eslint-disable */

declare namespace WELCOME {
  type Browser = {
    name?: number;
    value?: string;
  };

  type Statistic = {
    browser_datas?: Browser[];
    browser_legendData?: string[];
    currDate_pv?: number;
    currDate_uv?: number;
    currHour_uv?: number;
    currMonth_pv?: number;
    currWeek_pv?: number;

    operatingSystem_datas?: Browser[];
    operatingSystem_legendData?: string[];
    statDate_items?: Date[];
    statDate_pv?: number[];
    statDate_uv?: number[];
    statWeek_items?: Date[];
    statWeek_pv?: number[];
    statWeek_uv?: number[];
  };

  type AreaData = {
    type: string;
    date: Date;
    value: number;
  };
}
