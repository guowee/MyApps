package com.haomee.consts;

import com.haomee.liulian.LiuLianApplication;

/**
 * 文件路径
 */
public class PathConst {

	public static final String ROOT_PATH = "/LiuLian"; // 应用根目录

	public static final String IMAGE_CACHDIR = ROOT_PATH + "/imgCache/"; // 图片缓存目录
	public static final String DIR_TEMP = ROOT_PATH + "/temp/"; // 临时
	public static final String DIR_LOG = ROOT_PATH + "/log/"; // 日志文件

	public static final String DIR_USER_IMG = ROOT_PATH + "/我的截图/"; // 用户截屏
	public static final String DOWNLOAD_EMOTIONS=ROOT_PATH+"/emotions/";//表情目录
	// 脏词库文件
	public static final String BAD_WORDS_PATH = ROOT_PATH + "/badwords/";// 臧词路径
	public static final String BAD_WORDS_FILE = "liulian_bad_words.json";// 臧词文件
	// 离线数据对应路径开始
	public static final String OFFLINE_DATA = ROOT_PATH + "/offlineData/"; // 离线数据（没有网络时的缓存数据）
	public static final String OFFLINE_TOPIC_NEWEST = "topic_newest.json";
	public static final String OFFLINE_TOPIC_FAV = "topic_fav.json";
	public static final String OFFLINE_TOPIC_HISTORY = "topic_history.json";
	public static final String OFFLINE_TOPIC_PRAISE = "topic_praise.json";
	public static final String OFFLINE_TOPIC_SEARCH = "topic_search.json";
	public static final String OFFLINE_CONTENT = "content_list.json";
	public static final String OFFLINE_PERSON = "person_page.json";
	public static final String OFFLINE_PERSON_TOPIC = "person_page_topic.json";

	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final int CROPIMAGES = 4;
	public static final String IMAGE_UNSPECIFIED = "image/*";
	// 离线数据对应路径结束

	/** 网络路径 */
	public static String ENV_URL_ROOT = "http://api.durian.haomee.cn/?pf=1&android_version=" + android.os.Build.VERSION.SDK_INT + "&app_version=" + LiuLianApplication.appVersion + "&app_channel=" + LiuLianApplication.channelName_encode + "&app_language="
			+ LiuLianApplication.app_language + "&";// 网络根路径

	// public static final String ENV_URL_ROOT =
	// "http://api.durian.haomee.cn/?pf=1&android_version=" +
	// android.os.Build.VERSION.SDK_INT +"&app_version=" +
	// LiuLianApplication.appVersion + "&app_channel=" +
	// LiuLianApplication.channelName_encode + "&";// 网络根路径

	/*
	 * public static final String PREFIX_URL_AD =
	 * "http://ad.haomee.net/?m=Api&pf=1&app_id=6&app_key=2945d232e57235c7cc12a46cfe435df7&android_version="
	 * + android.os.Build.VERSION.SDK_INT + "&app_version=" +
	 * LiuLianApplication.appVersion + "&app_channel=" +
	 * LiuLianApplication.channelName_encode + "&device_id" +
	 * LiuLianApplication.deviceID; // 广告根路径
	 */public static final String URL_UPDATE = ENV_URL_ROOT + "m=Index&a=updateVersion"; // 版本更新

	 // public static final String ENV_URL_ROOT =
	 // "http://172.16.100.74/durian/api/?pf=1&";

	 public static final String URL_PERSON_HOME = ENV_URL_ROOT + "m=User&a=personHomePage"; // 我发的内容
	 public static final String URL_PERSON_HOME_TOPIC = ENV_URL_ROOT + "m=Useract&a=getJoin"; // 我聊过的话题

	 public static final String URL_REG_SD_CODE = ENV_URL_ROOT + "m=User&a=phoneRegisterSdCode";

	 public static final String URL_REG = ENV_URL_ROOT + "m=User&a=phoneRegister";

	 public static final String URL_LOGIN = ENV_URL_ROOT + "m=User&a=phoneLogin";

	 public static final String URL_PF_LOGIN = ENV_URL_ROOT + "m=User&a=pfLogin";

	 public static final String URL_RESET_PWD_CODE = ENV_URL_ROOT + "m=User&a=resetPwdSdCode";

	 public static final String URL_RESET_CHECK_CODE = ENV_URL_ROOT + "m=User&a=resetPwdCheckCode";

	 public static final String URL_RESET_PASS = ENV_URL_ROOT + "m=User&a=resetPwd";

	 public static final String URL_ADD_TOPIC = ENV_URL_ROOT + "m=Subject&a=add";

	 public static final String URL_CHECK_TOPIC_EXIST = ENV_URL_ROOT + "m=Subject&a=checkTopicName";

	 public static final String URL_EDIT_USER_INFO = ENV_URL_ROOT + "m=User&a=editUserInfo";

	 public static final String URL_PERSON_PAGE_HOME = ENV_URL_ROOT + "m=User&a=personHomePage";

	 public static final String URL_TOPIC_NEWEST = ENV_URL_ROOT + "m=Subject&a=newTopic";// 最新话题
	 // public static final String URL_TOPIC_REC = ENV_URL_ROOT +
	 // "m=Subject&a=recTopic";// 推荐话题

	 public static final String URL_TOPIC_ADD_ICONS = ENV_URL_ROOT + "m=Subject&a=icon";// 添加话题图标

	 public static final String URL_TOPIC_ADD_CONTENT = ENV_URL_ROOT + "m=Subject&a=addContent";// 发送话题内容

	 public static final String URL_SEARCH_MUSIC = ENV_URL_ROOT + "m=Subject&a=searchMusic";// 搜索音乐

	 public static final String URL_CONTENT_LIST = ENV_URL_ROOT + "m=Subject&a=getContent";// 内容列表页

	 public static final String URL_CONTENT_ADD = ENV_URL_ROOT + "m=Subject&a=addContent";// 添加内容列表页

	 public static final String URL_SEARCH_TOPIC = ENV_URL_ROOT + "m=Subject&a=searchTopic";// 搜索主题

	 public static final String URL_PRAISE_CONTENT = ENV_URL_ROOT + "m=Subject&a=praiseContent";// 点赞

	 public static final String URL_GET_USER_INFO_FROM_HX_NAME = ENV_URL_ROOT + "m=User&a=getUserInfoByHx";// 根据环信用户名拉取用户信息

	 public static final String URL_SAY_HI = ENV_URL_ROOT + "m=User&a=sayHi";// hi

	 public static final String URL_REPORT = ENV_URL_ROOT + "m=Useract&a=report";// 举报
	 public static final String URL_TOPIC_FAV = ENV_URL_ROOT + "m=Useract&a=focusTopic";// 收藏
	 public static final String URL_TOPIC_DEL_HISTORY = ENV_URL_ROOT + "m=Useract&a=delWatch";// 删除历史记录

	 public static final String URL_DEL_CONTENT = ENV_URL_ROOT + "m=Subject&a=delContent";// 删除内容

	 public static final String URL_LIST_FAV = ENV_URL_ROOT + "m=Useract&a=getFocus"; // 收藏列表
	 public static final String URL_LIST_HISTORY = ENV_URL_ROOT + "m=Useract&a=getWatched"; // 历史列表
	 public static final String URL_LIST_PRAISE = ENV_URL_ROOT + "m=Useract&a=getPraise"; // 点赞列表

	 public static final String URL_LIST_SYSTEM_MESSAGE = ENV_URL_ROOT + "m=Index&a=getSysmsg"; // 消息列表

	 public static final String URL_LIST_SYSTEM_MESSAGE_NEW_V3 = ENV_URL_ROOT + "m=Index&a=getSysmsg_v3"; // V3
	 // 系统消息

	 public static final String URL_SET_TOP = ENV_URL_ROOT + "m=Useract&a=stickTopic";// 置顶

	 public static final String URL_PUNCH_LIST = ENV_URL_ROOT + "m=Subject&a=getPunch"; // 打卡列表

	 public static final String URL_PUNCH = ENV_URL_ROOT + "m=Subject&a=punch";// 打卡

	 public static final String URL_HAVE_SYS = ENV_URL_ROOT + "m=Index&a=haveSysmsg";// 是否有系统消息

	 public static final String URL_HAVE_SYS_NEW_V3 = ENV_URL_ROOT + "m=Index&a=haveSysmsg_v3";// 是否有系统消息

	 public static final String URL_SEARCH_MOVIE = ENV_URL_ROOT + "m=Subject&a=searchMovie";// 搜索音乐

	 public static final String URL_SEARCH_HOT_MOVIE = ENV_URL_ROOT + "m=Subject&a=hotMovie";// 热门电影

	 public static final String URL_SEARCH_HOT_MUSIC = ENV_URL_ROOT + "m=Subject&a=hotMusic";// 热门音乐

	 public static final String URL_SEARCH_RANGE_TOPIC = ENV_URL_ROOT + "m=Subject&a=getRangeTopic"; // 搜索区域话题

	 public static final String URL_SEARCH_TRY_SEARCH = ENV_URL_ROOT + "m=Subject&a=trySearch"; // 随机话题

	 public static final String URL_FEEDFACK = ENV_URL_ROOT + "m=Index&a=feedback";// 意见反馈

	 public static final String URL_LIANXIANGCI = ENV_URL_ROOT + "m=Subject&a=inspiration"; // 联想词

	 public static final String URL_GET_CONTENT = ENV_URL_ROOT + "m=Subject&a=getContentInfo"; // 内容详情

	 public static final String URL_GET_MEDAL = ENV_URL_ROOT + "m=Subject&a=egg";// 获取彩蛋列表

	 public static final String URL_ADD_MEDAL = ENV_URL_ROOT + "m=Subject&a=addEgg";// 添加本地彩蛋

	 public static final String URL_LOGIN_LOG = ENV_URL_ROOT + "m=User&a=userLoginLog";// 用户登陆日志统计

	 public static final String URL_USER_PORTAL = "http://api.durian.haomee.cn/html/protocol/userPortal.html";

	 public static final String URL_USER_PRIVACY = "http://api.durian.haomee.cn/html/protocol/userPrivacyPortal.html";

	 public static final String URL_USER_DEL_MSG = ENV_URL_ROOT + "m=Index&a=delSysmsg"; // 删除系统消息

	 public static final String URL_RADAR_NEARBY = ENV_URL_ROOT + "m=Interest&a=peopleNearBy";// 雷达
	 public static final String URL_MAP_NEARBY = ENV_URL_ROOT + "m=Index&a=peopleNearByMap";// 地图

	 public static final String URL_OTHER_USER_DETAIL = ENV_URL_ROOT + "m=User&a=otherHomePage_new";

	 // public static final String URL_TOPIC_REC_NEW = ENV_URL_ROOT +
	 // "m=Index&a=recTopic";// 推荐话题 // 新的

	 public static final String URL_TOPIC_REC = ENV_URL_ROOT + "m=Interest&a=InterestList";// 推荐话题

	 public static final String URL_LATEST_LOGIN = ENV_URL_ROOT + "m=Index&a=latestLogin";// 最近登录

	 public static final String URL_BACK_IMG_LIST = ENV_URL_ROOT + "m=Index&a=topicBackImgList"; // 话题背景图

	 public static final String URL_MY_PERSON_HOME = ENV_URL_ROOT + "m=User&a=myHomePage"; // 我发的内容

	 public static final String URL_DEL_IMAGE = ENV_URL_ROOT + "m=User&a=delPhoto"; // 删除图片

	 public static final String URL_QUESTIONS_RANDOM = ENV_URL_ROOT + "m=Index&a=chatQuestion"; // 获取随机问题

	 public static final String URL_PERSON_LABEL = ENV_URL_ROOT + "m=User&a=myHomePageLabel&uid=";

	 public static final String URL_RAND_UAERNAME = ENV_URL_ROOT + "m=Index&a=randUsername";

	 public static final String URL_USER_FOCUS = ENV_URL_ROOT + "m=User&a=doFocus"; // 关注/取消关注

	 public static final String URL_INTEREST_USERS = ENV_URL_ROOT + "m=Interest&a=getInterestUser"; // 关注/取消关注

	 public static final String URL_MY_INTEREST = ENV_URL_ROOT + "m=User&a=myHomePage_interest";

	 public static final String URL_MY_IDOLS = ENV_URL_ROOT + "m=User&a=myFocus&uid=";// 我的关注列表

	 public static final String URL_PRIFERENCE_COMIT = ENV_URL_ROOT + "m=Interest&a=addInterest"; // 添加兴趣

	 public static final String URL_REPORT_COMIT = ENV_URL_ROOT + "m=Useract&a=report";// 举报

	 public static final String URL_LABEL_USERS_LIST = ENV_URL_ROOT + "m=Interest&a=getUserByCategory"; // 用户标签列表

	 public static final String URL_INTEREST_ADD = ENV_URL_ROOT + "m=Interest&a=addInterestUser&id="; // 添加兴趣用户

	 public static final String URL_INTEREST_DELETE = ENV_URL_ROOT + "m=Interest&a=delInterestUser"; // 删除兴趣用户

	 // http://api.durian.haomee.cn/?m=Index&a=search&name=php//搜索
	 // http://api.durian.haomee.cn/?m=Index&a=search&name=%E6%80%A1%E7%84%B6
	 public static final String URL_TOPIC_TYPE_SEARCH = ENV_URL_ROOT + "m=Index&a=search";// 搜索詞
	 // http://api.durian.haomee.cn/?m=Index&a=getHotWords//熱門搜索詞
	 public static final String URL_TOPIC_TYPE_HOTSEARCH = ENV_URL_ROOT + "m=Index&a=getHotWords";// 熱門搜索詞
	 // http://api.durian.haomee.cn/?m=Index&a=badwords
	 public static final String URL_BAD_WORDS = ENV_URL_ROOT + "m=Index&a=badwords";//脏次
//	 http://api.durian.haomee.cn/?m=Index&a=newBadwords
	 public static final String URL_BAD_WORDS_NEW=ENV_URL_ROOT+"m=Index&a=newBadwords";

	 public static final String URL_NOT_SHOW = ENV_URL_ROOT + "m=Interest&a=notShowInterest";// 发现页取消关注
	 // http://api.durian.haomee.cn/?m=Question&a=getQuestionList&Luid=1
	 public static final String URL_LEVER_TEXT = ENV_URL_ROOT + "m=Question&a=getQuestionList";// 测试
	 // http://api.durian.haomee.cn/?m=Question&a=answerQuestion&Luid=1&qid=1&item_id=1
	 public static final String URL_QUESTION_ANSWER = ENV_URL_ROOT + "m=Question&a=answerQuestion";// 回答
	 // http://api.durian.haomee.cn/?m=Question&a=getResult&Luid=225
	 public static final String URL_QUESTION_RESULT = ENV_URL_ROOT + "m=Question&a=getResult";// 答题结果
	 // String
	 // url="http://api.durian.haomee.cn/?m=Question&a=getUserQuestion&uid=225"
	 public static final String URL_GET_USER_QUESTION = ENV_URL_ROOT + "m=Question&a=getUserQuestion&uid=";// 匹配测试
	 public static final String URL_INTEREST_TOPICS_ADD = ENV_URL_ROOT + "m=Interest&a=postInterestText"; // 添加兴趣话题新接口
	 public static final String URL_ANSWER_USER_TEST = ENV_URL_ROOT + "m=Question&a=answerUserTest";
	 public static final String URL_CREATE_GAME = ENV_URL_ROOT + "m=Game&a=startGame";// 创建游戏

	 public static final String URL_INTEREST_FILTER = ENV_URL_ROOT + "m=Interest&a=interestFielterUser";// 筛一筛接口

	 public static final String URL_GAME_IMG = "http://cdn.haomee.cn/durian/head/game_lever1_";// 游戏头像
	 public static final String URL_GAME_RESULT_SUBMIT = ENV_URL_ROOT + "m=Game&a=finishGame";// 上传游戏结果

	 public static final String URL_GETALLPEOPLE = ENV_URL_ROOT + "m=Question&a=getAllpeople";// 查看全部人接口
	 public static final String URL_GETITEMPEOPLE = ENV_URL_ROOT + "m=Question&a=getItemPeople";// 查看某个用户接口
	 public static final String URL_TWO_PEOPLE__FIRST_CHAT = ENV_URL_ROOT + "m=Index&a=setCanTalkState";// 两个用户第一次聊天
//	 http://api.durian.haomee.cn/?m=Emoji&a=emojiList
	 public static final String URL_EMOTIONS=ENV_URL_ROOT+"m=Emoji&a=emojiList";//表情下载列表

	 public static final String URL_EXPRESS_PREFIX = "http://cdn.haomee.cn/durian/emotion/";
	 public static final String URL_EMOTION_FIRST="http://cdn.haomee.cn/durian/emotion//zip/0.zip";
	 public static final String URL_EMOTION_SECOND="http://cdn.haomee.cn/durian/emotion//zip/3.zip";
	 public static final String URL_EMOTION_THRID="http://cdn.haomee.cn/durian/emotion//zip/4.zip";
}
