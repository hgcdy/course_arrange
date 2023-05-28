package cn.netinnet.coursearrange.util;

/**
 *
 * @author ousp
 */
public class NameUtil {
    /**
     * 姓
     **/
    private static final String[] FAMILY_NAMES = new String[]{
            "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "蒋", "沈",
            "韩", "杨", "柏", "水", "窦", "章", "何", "吕", "施", "张",
            "钟", "徐", "邱", "骆", "贾", "路", "娄", "危", "鲁", "韦",
            "昌", "马", "祁", "毛", "禹", "狄", "孔", "曹", "严", "华",
            "杜", "阮", "蓝", "闵", "滕", "殷", "罗", "毕", "俞", "任",
            "袁", "柳", "江", "童", "颜", "郭", "樊", "胡", "凌", "霍",
            "梅", "盛", "林", "刁", "高", "夏", "蔡", "田", "乐", "于",
            "时", "傅", "伍", "余", "元", "卜", "顾", "孟", "平", "黄",
            "和", "穆", "萧", "尹", "姚", "邵", "湛", "汪", "席", "季",
            "麻", "强", "郝", "邬", "安", "常", "雷", "贺", "倪", "汤"};
    /**
     * 名
     **/
    private static final String[] GIVEN_NAMES = new String[]{
            "景", "天", "清", "馨", "阳", "英", "耀", "巧", "夏", "智",
            "潍", "梦", "月", "秋", "萱", "寒", "蕾", "琴", "韵", "如",
            "昌", "茂", "雅", "娴", "灵", "北", "辰", "忆", "尔", "容",
            "南", "风", "凝", "静", "娜", "兰", "小", "霜", "冰", "欣",
            "衍", "语", "晨", "嘉", "岚", "琅", "乐", "怡", "晖", "宇",
            "佳", "雨", "宏", "伟", "鸿", "畅", "永", "年", "宜", "春",
            "笑", "映", "雁", "俊", "玉", "秀", "曼", "润", "梅", "青",
            "翠", "柏", "晶", "文", "德", "星", "海", "亦", "筠", "诗",
            "丽", "敏", "凡", "思", "宸", "蓉", "莺", "雪", "弘", "盛",
            "珺", "琦", "桦", "荷", "朋", "梓", "露", "真", "柔", "颐"
    };

    /***
    * 随机生成姓名
    */
    public static String getRandName() {
//        int nameLen = (int) (Math.random() * (2 - 1) + 1);
        int nameLen = (int) (Math.random() * 2 + 1);
        if (nameLen > 1) {
            return FAMILY_NAMES[(int) (Math.random() * FAMILY_NAMES.length)] + GIVEN_NAMES[(int) (Math.random() * GIVEN_NAMES.length)]
                    + GIVEN_NAMES[(int) (Math.random() * GIVEN_NAMES.length)];
        } else {
            return FAMILY_NAMES[(int) (Math.random() * FAMILY_NAMES.length)] + GIVEN_NAMES[(int) (Math.random() * GIVEN_NAMES.length)];
        }
    }
}
