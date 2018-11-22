package com.kery.mobilemanager.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */

public class Card implements Comparable<Card> {

//    {
//        "status": True,      //响应状态, True正常返回,False条件不足
//            "msg": "",           //status为False时具体错误消息说明
//            "data": {            //接口响应数据
//        "pages": [       //显示的标签列表
//        {
//            "sort": 1,                      //排序用标识
//                "content1": "当日未完成 0",       //文本1
//                "content2": "过期未完成 0",       //文本2
//                "type": "todo",                 //标签类型, 前端可根据此确定类型
//                "title": "待办事项"              //title
//        },
//        {
//            "sort": 2,
//                "title": "租务管理",
//                "next_pages": [                 //下一级页面内容
//            {
//                "sort": 101,
//                    "content1": "空置0间",
//                    "type": "house_manager",
//                    "title": "房源管理"
//            },
//            {
//                "sort": 102,
//                    "content1": "",
//                    "type": "read_meter",
//                    "title": "抄表"
//            },
//            {
//                "sort": 103,
//                    "content1": "0个逾期",
//                    "type": "customer_order",
//                    "title": "租客账单"
//            },
//            {
//                "sort": 104,
//                    "content1": "0个逾期",
//                    "type": "customer_contract",
//                    "title": "租客合同"
//            },
//            {
//                "sort": 105,
//                    "content1": "0个逾期",
//                    "type": "",
//                    "title": "业主账单"
//            },
//            {
//                "sort": 106,
//                    "content1": "0个逾期",
//                    "type": "",
//                    "title": "业主合同"
//            }
//            ],
//            "type": "rent_manager",
//                "content1": "总房源 0 间",
//                "content2": "未处理合同/账单 0/0"
//        },
//        {
//            "sort": 3,
//                "title": "智能硬件",
//                "next_pages": [
//            {
//                "sort": 201,
//                    "content1": "主表 0 个",
//                    "content2": "子表 0 个",
//                    "type": "smart_power",
//                    "title": "智能电表"
//            },
//            {
//                "sort": 202,
//                    "content1": "网关 0 个",
//                    "content2": "门锁 0 个",
//                    "type": "smart_lock",
//                    "title": "智能门锁"
//            },
//            {
//                "sort": 203,
//                    "content1": "网关 0 个",
//                    "content2": "水表 0 个",
//                    "type": "smart_water",
//                    "title": "智能水表"
//            }
//            ],
//            "type": "smart_device",
//                "content1": "门锁 0",
//                "content2": "电表/水表 0/0"
//        },
//        {
//            "sort": 4,
//                "content1": "当月收入 ¥0.00",
//                "content2": "当月支出 ¥0.00",
//                "type": "finance_flow",
//                "title": "财务流水"
//        },
//        {
//            "sort": 5,
//                "content1": "待分配 0",
//                "content2": "待跟进 0",
//                "type": "dispatch_center",
//                "title": "派单中心"
//        },
//        {
//            "sort": 6,
//                "content1": "13日 日报",
//                "content2": "12月 月报",
//                "type": "reports",
//                "title": "经营报表"
//        },
//        {
//            "sort": 7,
//                "content1": "",
//                "content2": "",
//                "type": "statistics",
//                "title": "运营分析"
//        }
//    ]
//    }
//    }


    public int sort;
    public String content1;
    public String content2;
    public String type;
    public String title;
    public int is_new;//1需要显示角标，0不需要
    public List<NextPage> next_pages;

    @Override
    public String toString() {
        return "Card{" +
                "sort=" + sort +
                ", content1='" + content1 + '\'' +
                ", content2='" + content2 + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", is_new=" + is_new +
                '}';
    }

    //倒序，从小到大
    @Override
    public int compareTo(@NonNull Card o) {
        return this.sort - o.sort;
    }

    public static class NextPage implements Parcelable {
        /**
         * sort : 201
         * content1 : 主表 0 个
         * content2 : 子表 0 个
         * type : smart_power
         * title : 智能电表
         */

        public int sort;
        public String content1;
        public String content2;
        public String type;
        public String title;
        public int reddot;
        public int is_new;

        @Override
        public String toString() {
            return "NextPage{" +
                    "sort=" + sort +
                    ", content1='" + content1 + '\'' +
                    ", content2='" + content2 + '\'' +
                    ", type='" + type + '\'' +
                    ", title='" + title + '\'' +
                    ", reddot=" + reddot +
                    ", is_new=" + is_new +
                    '}';
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.sort);
            dest.writeString(this.content1);
            dest.writeString(this.content2);
            dest.writeString(this.type);
            dest.writeString(this.title);
            dest.writeInt(this.reddot);
            dest.writeInt(this.is_new);
        }

        public NextPage() {
        }

        protected NextPage(Parcel in) {
            this.sort = in.readInt();
            this.content1 = in.readString();
            this.content2 = in.readString();
            this.type = in.readString();
            this.title = in.readString();
            this.reddot = in.readInt();
            this.is_new = in.readInt();
        }

        public static final Creator<NextPage> CREATOR = new Creator<NextPage>() {
            @Override
            public NextPage createFromParcel(Parcel source) {
                return new NextPage(source);
            }

            @Override
            public NextPage[] newArray(int size) {
                return new NextPage[size];
            }
        };
    }

}
