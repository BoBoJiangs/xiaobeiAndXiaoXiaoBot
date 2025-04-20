//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package top.sshh.qqbot.service;

import com.zhuangxv.bot.annotation.GroupMessageHandler;
import com.zhuangxv.bot.config.BotConfig;
import com.zhuangxv.bot.core.Bot;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.core.Member;
import com.zhuangxv.bot.core.component.BotFactory;
import com.zhuangxv.bot.message.Message;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.message.support.AtMessage;
import com.zhuangxv.bot.message.support.ReplyMessage;
import com.zhuangxv.bot.message.support.TextMessage;
import com.zhuangxv.bot.utilEnum.IgnoreItselfEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.sshh.qqbot.data.ProductPrice;

@Component
public class XiaoBeiService {
    private static final Logger log = LoggerFactory.getLogger(XiaoBeiService.class);
    @Autowired
    private ProductPriceResponse productPriceResponse;
    private static final ForkJoinPool customPool = new ForkJoinPool(20);
    public static final Map<Long, Map<String, ProductPrice>> AUTO_BUY_PRODUCT = new ConcurrentHashMap();
    public static final String PRODUCT_NAME = "#明心问道果#离火梧桐芝#剑魄竹笋#尘磊岩麟果#天剑破虚#仙火焚天#千慄鬼噬#佛怒火莲#合欢魔功#";
    public static final String SECRET_END_NAME = "在秘境最深处#道友在秘境#进入秘境#秘境内竟然#道友大战一番成功#道友大战一番不敌#仅为空手文案";
    public static final String MODE_NAME = "邪修抢夺灵石#私自架设小型窝点#被追打催债#秘境内竟然#请道友下山购买#为宗门购买一些#速去仙境抢夺仙境之石#义字当头";
    @Value("${xbGroupId}")
    private Long xbGroupId;
    public XiaoBeiService() {
    }

    public static void proccessCultivation(Group group, BotConfig botConfig) {
        botConfig.setBeiFamilyTaskStatus(0);
        botConfig.setXslBeiTime(-1L);
        botConfig.setMjBeiTime(-1L);
        group.sendMessage((new MessageChain()).at("3889029313").text("闭关"));
    }

    @GroupMessageHandler(
            ignoreItself = IgnoreItselfEnum.ONLY_ITSELF
    )
    public void enableScheduled(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
        message = message.trim();
        if ("开始小北自动宗门任务".equals(message)) {
            botConfig.setBeiFamilyTaskStatus(1);
        }

        if ("停止小北自动宗门任务".equals(message)) {
            botConfig.setBeiFamilyTaskStatus(0);
        }

        if (message.startsWith("/")) {
            String number = message.substring(message.indexOf("/") + 1).trim();
            group.sendMessage((new MessageChain()).at("3889029313").text("查询世界BOSS "+number));
        }

        if ("小北自动药材上架".equals(message)) {
            botConfig.setCommand("小北自动药材上架");
            group.sendMessage((new MessageChain()).at("3889029313").text("药材"));
        }

        if (message.endsWith("小北药材上架")) {
            List<ReplyMessage> replyMessageList = messageChain.getMessageByType(ReplyMessage.class);
            if (replyMessageList != null && !replyMessageList.isEmpty()) {
                ReplyMessage replyMessage = (ReplyMessage)replyMessageList.get(0);
                MessageChain replyMessageChain = replyMessage.getChain();
                if (replyMessageChain != null) {
                    List<TextMessage> textMessageList = replyMessageChain.getMessageByType(TextMessage.class);
                    if (textMessageList != null && !textMessageList.isEmpty()) {
                        TextMessage textMessage = (TextMessage)textMessageList.get(textMessageList.size() - 1);
                        String herbsInfo = textMessage.getText();
                        String[] lines = herbsInfo.split("\n");
                        Map<String, Integer> herbs = extractHerbs(herbsInfo);
                        Iterator var16 = herbs.entrySet().iterator();

                        while(var16.hasNext()) {
                            Map.Entry<String, Integer> entry = (Map.Entry)var16.next();
                            if (botConfig.isStop()) {
                                botConfig.setStop(false);
                                break;
                            }

                            group.sendMessage((new MessageChain()).at("3889029313").text("坊市上架 " + (String)entry.getKey() + " " + 1 + " " + entry.getValue()));
                            Thread.sleep(3000L);

                        }
                    }
                }

                botConfig.setCommand("");
            }
        }

    }

    @GroupMessageHandler(
            ignoreItself = IgnoreItselfEnum.NOT_IGNORE
    )
    public void 弟子执行小北命令(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) {
        boolean isControlQQ = false;
        if (StringUtils.isNotBlank(bot.getBotConfig().getControlQQ())) {
            isControlQQ = ("&" + bot.getBotConfig().getControlQQ() + "&").contains("&" + member.getUserId() + "&");
        } else {
            isControlQQ = bot.getBotConfig().getMasterQQ() == member.getUserId();
        }

        if (isControlQQ && message.contains("#")) {
            Iterator iterator = messageChain.iterator();

            while(iterator.hasNext()) {
                Message timeMessage = (Message)iterator.next();
                if (!(timeMessage instanceof AtMessage)) {
                    break;
                }

                iterator.remove();
            }

            message = ((TextMessage)messageChain.get(0)).getText().trim();
            if (message.startsWith("#")) {
                message = message.substring(message.indexOf("#") + 1);
                messageChain.set(0, new TextMessage(message));
                messageChain.add(0, new AtMessage("3889029313"));
                group.sendMessage(messageChain);
            }
        }

    }

    @GroupMessageHandler(
            ignoreItself = IgnoreItselfEnum.NOT_IGNORE
    )
    public void 讨伐世界boss(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) {
        boolean isControlQQ = bot.getBotConfig().getMasterQQ() == member.getUserId();
//        if (StringUtils.isNotBlank(bot.getBotConfig().getControlQQ())) {
//            isControlQQ = ("&" + bot.getBotConfig().getControlQQ() + "&").contains("&" + member.getUserId() + "&");
//        } else {
//            isControlQQ = bot.getBotConfig().getMasterQQ() == member.getUserId();
//        }

        BotConfig botConfig = bot.getBotConfig();
        boolean isGroup = isXbGroup(group,botConfig);
        if (isGroup && isControlQQ && (message.contains("讨伐世界boss") || message.contains("讨伐世界BOSS")) && botConfig.getMasterQQ() != bot.getBotId()) {
            Pattern pattern = Pattern.compile("讨伐世界boss(\\d+)");
            Matcher matcher = pattern.matcher(message);
            String text = "";
            if (matcher.find()) {
                text = "讨伐世界boss" + matcher.group(1);
            }

            if (StringUtils.isNotBlank(text)) {
                group.sendMessage((new MessageChain()).at("3889029313").text(text));
            }
        }

    }

    private boolean isXbGroup(Group group,BotConfig botConfig){
        boolean isGroup;
        if(xbGroupId == 0){
            isGroup = group.getGroupId() == botConfig.getGroupId() || group.getGroupId() == botConfig.getTaskId();
        }else{
            isGroup = group.getGroupId() == xbGroupId;
        }
        return isGroup;
    }

    public static Map<String, Integer> extractHerbs(String input) {
        Map<String, Integer> result = new LinkedHashMap();
        Pattern pattern = Pattern.compile("(.+?)\\s+-.+数量:\\s+(\\d+)");
        Matcher matcher = pattern.matcher(input);

        for(int count = 0; matcher.find() && count < 10; ++count) {
            String herbName = matcher.group(1).trim();
            int quantity = Integer.parseInt(matcher.group(2).trim());
            result.put(herbName, quantity);
        }

        return result;
    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 小北药材上架(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) {
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isAtSelf && message.contains("的药材背包")) {
            System.out.println(message);
            BotConfig botConfig = bot.getBotConfig();
            if (StringUtils.isNotBlank(botConfig.getCommand()) && botConfig.getCommand().equals("小北自动药材上架")) {
                group.sendMessage((new MessageChain()).reply(messageId).text("小北药材上架"));
            }
        }

    }

    @Scheduled(
            cron = "*/5 * * * * *"
    )
    public void 宗门任务接取刷新() throws InterruptedException {
        BotFactory.getBots().values().forEach((bot) -> {
            BotConfig botConfig = bot.getBotConfig();
            long groupId = botConfig.getGroupId();
            if (botConfig.getTaskId() != 0L) {
                groupId = botConfig.getTaskId();
            }

            if(xbGroupId != 0){
                groupId = xbGroupId;
            }

            Group group = bot.getGroup(groupId);
            switch (botConfig.getBeiFamilyTaskStatus()) {
                case 0:
                    return;
                case 1:
                    group.sendMessage((new MessageChain()).at("3889029313").text("宗门任务接取"));
                    botConfig.setBeiFamilyTaskStatus(2);
                    return;
                case 2:
                    group.sendMessage((new MessageChain()).at("3889029313").text("宗门任务完成"));
                    botConfig.setBeiFamilyTaskStatus(1);
                    return;
                default:
            }
        });
    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 宗门任务状态管理(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
//        boolean isGroup = group.getGroupId() == botConfig.getGroupId() || group.getGroupId() == botConfig.getTaskId();
        boolean isGroup = isXbGroup(group, botConfig);
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isGroup && isAtSelf) {
            if (message.contains("今日无法再获取宗门任务")) {
                proccessCultivation(group, botConfig);
            }

            if (message.contains("请检查该道具是否在背包内")||message.contains("道友不满足使用条件")) {
                botConfig.setBeiFamilyTaskStatus(0);
            }

            if (message.contains("灵石带少了") && message.contains("出门做任务") && message.contains("不扣你任务次数")) {
                botConfig.setBeiFamilyTaskStatus(0);
            }

            if (message.contains("状态欠佳") && message.contains("出门做任务") && message.contains("不扣你任务次数")) {
                group.sendMessage((new MessageChain()).at("3889029313").text("使用道源丹"));
                botConfig.setBeiFamilyTaskStatus(1);
            }
        }

    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 秘境(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
        LocalDateTime now = LocalDateTime.now();
//        boolean isGroup = group.getGroupId() == botConfig.getGroupId() || group.getGroupId() == botConfig.getTaskId();
        boolean isGroup = isXbGroup(group, botConfig);
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isGroup && isAtSelf) {
            if (message.contains("参加过本次所有秘境")) {
                proccessCultivation(group, botConfig);
            }

            if (message.contains("道友现在什么都没干")) {
            }

            String[] parts;
            if (message.contains("进行中的：") && message.contains("可结束") && message.contains("探索")) {
                if (message.contains("(原")) {
                    parts = message.split("预计|\\(原");
                } else {
                    parts = message.split("预计|分钟");
                }

                System.out.println("秘境结算时间" + parts[1]);
                botConfig.setMjBeiTime((long)(Double.parseDouble(parts[1]) * 60.0 * 1000.0 + (double)System.currentTimeMillis()));
            }

            if (message.contains("进入秘境") && message.contains("探索需要花费")) {
                if (message.contains("(原")) {
                    parts = message.split("花费时间：|\\(原");
                } else {
                    parts = message.split("花费时间：|分钟");
                }

                System.out.println("秘境结算时间" + parts[1]);
                botConfig.setMjBeiTime((long)(Double.parseDouble(parts[1]) * 60.0 * 1000.0 + (double)System.currentTimeMillis()));
            }
        }

    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 悬赏令(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
//        boolean isGroup = group.getGroupId() == botConfig.getGroupId() || group.getGroupId() == botConfig.getTaskId();
        boolean isGroup = isXbGroup(group, botConfig);
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isGroup && isAtSelf) {
            if (message.contains("在做悬赏令呢") && message.contains("分身乏术")) {
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令结算"));
            }

            if (message.contains("没有查到你的悬赏令信息")) {
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令"));
            }

            if (message.contains("当前没有进行中的悬赏令任务")) {
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令刷新"));
            }

            if (message.contains("悬赏令刷新次数已用尽") || message.contains("道友的免费刷新次数已耗尽")) {
                botConfig.setXslBeiTime(-1L);
                proccessCultivation(group, botConfig);
            }

            if (message.contains("进行中的悬赏令") && message.contains("可结束")) {
                String[] parts;
                if (message.contains("(原")) {
                    parts = message.split("预计|\\(原");
                } else {
                    parts = message.split("预计|分钟");
                }

                botConfig.setXslBeiTime((long)(Math.ceil(Double.parseDouble(parts[1])) * 60.0 * 1000.0 + (double)System.currentTimeMillis()));
            }

            if (message.contains("进行中的悬赏令") && message.contains("已结束")) {
                botConfig.setXslBeiTime(-1L);
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令结算"));
            }

            if (message.contains("接取任务") && message.contains("成功")) {
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令结算"));
            }
        }

    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 悬赏令接取(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
//        boolean isGroup = group.getGroupId() == botConfig.getGroupId() || group.getGroupId() == botConfig.getTaskId();
        boolean isGroup = isXbGroup(group, botConfig);
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isGroup && isAtSelf && message.contains("发布悬赏令如下")) {
            Iterator<TextMessage> textMessageIterator = messageChain.getMessageByType(TextMessage.class).iterator();
            group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令接取"+selectBestTask(message)));
//            while(textMessageIterator.hasNext()) {
//                TextMessage textMessage = (TextMessage)textMessageIterator.next();
//                String text = textMessage.getText();
//                if (text.contains("发布悬赏令如下")) {
//                    int receIndex = this.processRewardText(text);
//                    this.sendRewardMessage(group, receIndex);
//                }
//            }
        }

    }

    public static int selectBestTask(String rewardText) {
        List<Task> tasks = parseTasks(rewardText);
        if (tasks.isEmpty()) {
            return -1;
        }

        // 优先级物品列表（从上到下优先级递减）
        String[] priorityItems = {
                "佛怒火莲", "天剑破虚", "仙火焚天", "千慄鬼噬", "明心问道果",
                "离火梧桐芝", "剑魄竹笋", "尘磊岩麟果","风神诀", "合欢魔功",
        };

        // 1. 检查是否有优先物品
        for (String item : priorityItems) {
            for (Task task : tasks) {
                if (task.getRewardItem().contains(item)) {
                    return task.getNumber();
                }
            }
        }

        // 2. 否则选择修为最高的任务
        long maxExp = -1;
        int bestTask = -1;
        for (Task task : tasks) {
            if (task.getExp() > maxExp) {
                maxExp = task.getExp();
                bestTask = task.getNumber();
            }
        }
        return bestTask;
    }

    private static List<Task> parseTasks(String rewardText) {
        List<Task> tasks = new ArrayList<>();
        String[] lines = rewardText.split("\n");
        int currentTaskNumber = -1;
        String currentTaskName = null;
        long currentExp = -1;
        String currentRewardItem = null;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("☆") || line.startsWith(">")) {
                continue;
            }

            // 检测任务编号行（如 "1、寻找九叶芝, 完成机率 100%..."）
            if (line.matches("^\\d+、.+")) {
                // 保存上一个任务（如果有）
                if (currentTaskNumber != -1) {
                    tasks.add(new Task(currentTaskNumber, currentTaskName, currentExp, currentRewardItem));
                }
                // 解析新任务
                String[] parts = line.split("、", 2);
                currentTaskNumber = Integer.parseInt(parts[0]);
                String rest = parts[1];

                // 解析任务名称（如 "寻找九叶芝"）
                currentTaskName = rest.split(",")[0].trim();

                // 解析修为（如 "基础报酬 400 修为"）
                String expPart = rest.split("基础报酬")[1].split("修为")[0].trim();
                currentExp = Long.parseLong(expPart.replaceAll("[^0-9]", ""));

                // 解析奖励物品（如 "可能额外获得：一品药材:红绫草"）
                if (rest.contains("可能额外获得：")) {
                    currentRewardItem = rest.split("可能额外获得：")[1].trim();
                }
            }
        }

        // 添加最后一个任务
        if (currentTaskNumber != -1) {
            tasks.add(new Task(currentTaskNumber, currentTaskName, currentExp, currentRewardItem));
        }

        return tasks;
    }

    static class Task {
        private final int number;
        private final String name;
        private final long exp;
        private final String rewardItem;

        public Task(int number, String name, long exp, String rewardItem) {
            this.number = number;
            this.name = name;
            this.exp = exp;
            this.rewardItem = rewardItem;
        }

        public int getNumber() { return number; }
        public long getExp() { return exp; }
        public String getRewardItem() { return rewardItem; }
    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 结算(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
        long groupId = botConfig.getGroupId();
//        boolean isGroup = group.getGroupId() == botConfig.getGroupId() || group.getGroupId() == botConfig.getTaskId();
        boolean isGroup = isXbGroup(group, botConfig);
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isGroup && isAtSelf && message.contains("悬赏令结算") && message.contains("增加修为")) {
            bot.getBotConfig().setXslTime(-1L);
            group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令刷新"));
        }

    }

    public static boolean containsSpecificTexts(String text, List<String> specificTexts) {
        Iterator var2 = specificTexts.iterator();

        String specificText;
        do {
            if (!var2.hasNext()) {
                return false;
            }

            specificText = (String)var2.next();
        } while(!text.contains(specificText));

        return true;
    }

    @Scheduled(
            fixedDelay = 60000L,
            initialDelay = 3000L
    )
    public void 结算() {
        BotFactory.getBots().values().forEach((bot) -> {
            BotConfig botConfig = bot.getBotConfig();
            if (botConfig.getMjBeiTime() > 0L && botConfig.getMjBeiTime() < System.currentTimeMillis()) {
                botConfig.setMjTime(-1L);
                long groupId = botConfig.getGroupId();
                if (botConfig.getTaskId() != 0L) {
                    groupId = botConfig.getTaskId();
                }

                if (xbGroupId != 0){
                    groupId = xbGroupId;
                }

                bot.getGroup(groupId).sendMessage((new MessageChain()).at("3889029313").text("秘境结算"));

                try {
                    Thread.sleep(5000L);
                } catch (Exception var5) {
                }

                bot.getGroup(groupId).sendMessage((new MessageChain()).at("3889029313").text("探索秘境"));
            }

        });
        BotFactory.getBots().values().forEach((bot) -> {
            BotConfig botConfig = bot.getBotConfig();
            if (botConfig.getXslBeiTime() > 0L && botConfig.getXslBeiTime() < System.currentTimeMillis()) {
                long groupId = botConfig.getGroupId();
                if (botConfig.getTaskId() != 0L) {
                    groupId = botConfig.getTaskId();
                }
                if (xbGroupId != 0){
                    groupId = xbGroupId;
                }

                bot.getGroup(groupId).sendMessage((new MessageChain()).at("3889029313").text("悬赏令结算"));
            }

        });
    }

    @GroupMessageHandler(
            senderIds = {3889029313L}
    )
    public void 灵田领取结果(Bot bot, Group group, Member member, MessageChain messageChain, String message, Integer messageId) throws InterruptedException {
        BotConfig botConfig = bot.getBotConfig();
        boolean isGroup = isXbGroup(group,botConfig);
        boolean isAtSelf = message.contains("" + bot.getBotId()) || message.contains(bot.getBotName());
        if (isGroup && isAtSelf) {
            if (message.contains("灵田还不能收取")) {
                String[] parts = message.split("：|小时");
                if (parts.length < 2) {
                    group.sendMessage((new MessageChain()).text("输入格式不正确，请确保格式为 '下次收取时间为：XX.XX小时"));
                    return;
                }

                double hours = Double.parseDouble(parts[1].trim());
                bot.getBotConfig().setLastBeiExecuteTime((long)((double)System.currentTimeMillis() + hours * 60.0 * 60.0 * 1000.0));
                group.sendMessage((new MessageChain()).text("下次收取时间为：" + FamilyTask.sdf.format(new Date(bot.getBotConfig().getLastBeiExecuteTime()))));
            } else if (message.contains("还没有洞天福地")) {
                System.out.println(LocalDateTime.now() + " " + group.getGroupName() + " 收到灵田领取结果,还没有洞天福地");
                bot.getBotConfig().setLastBeiExecuteTime(9223372036854175807L);
            }
        }

    }

    @Scheduled(
            cron = "0 */1 * * * *"
    )
    public void 灵田领取() throws InterruptedException {
        Iterator var1 = BotFactory.getBots().values().iterator();

        while(var1.hasNext()) {
            Bot bot = (Bot)var1.next();
            BotConfig botConfig = bot.getBotConfig();
            if (botConfig.getLastBeiExecuteTime() + 60000L < System.currentTimeMillis()) {
                long groupId = botConfig.getGroupId();
                if (botConfig.getTaskId() != 0L) {
                    groupId = botConfig.getTaskId();
                }
                if (xbGroupId != 0){
                    groupId = xbGroupId;
                }

                Group group = bot.getGroup(groupId);
                group.sendMessage((new MessageChain()).at("3889029313").text("灵田结算"));
            }
        }

    }

    @Scheduled(
            cron = "0 1 4 * * *"
    )
    public void 定时悬赏令刷新() {
        System.out.println("悬赏令定时任务执行啦！");
        Iterator var1 = BotFactory.getBots().values().iterator();

        while(var1.hasNext()) {
            Bot bot = (Bot)var1.next();
            BotConfig botConfig = bot.getBotConfig();
            long groupId = botConfig.getGroupId();
            if (botConfig.getTaskId() != 0L) {
                groupId = botConfig.getTaskId();
            }
            if (xbGroupId != 0){
                groupId = xbGroupId;
            }

            Group group = bot.getGroup(groupId);

            try {
                group.sendMessage((new MessageChain()).at("3889029313").text("出关"));
                Thread.sleep(5000L);
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令刷新"));
                Thread.sleep(5000L);
                group.sendMessage((new MessageChain()).at("3889029313").text("悬赏令"));
            } catch (Exception var8) {
            }
        }

    }

    @Scheduled(
            cron = "0 31 12 * * *"
    )
    public void 定时探索秘境() {
        System.out.println("秘境定时任务执行啦！");
        Iterator var1 = BotFactory.getBots().values().iterator();

        while(var1.hasNext()) {
            Bot bot = (Bot)var1.next();
            BotConfig botConfig = bot.getBotConfig();
            long groupId = botConfig.getGroupId();
            if (botConfig.getTaskId() != 0L) {
                groupId = botConfig.getTaskId();
            }
            if (xbGroupId != 0){
                groupId = xbGroupId;
            }

            Group group = bot.getGroup(groupId);

            try {
                group.sendMessage((new MessageChain()).at("3889029313").text("出关"));
                Thread.sleep(5000L);
                group.sendMessage((new MessageChain()).at("3889029313").text("探索秘境"));
            } catch (Exception var8) {
            }
        }

    }

    @Scheduled(
            cron = "0 1 0 * * *"
    )
    public void 定时宗门任务() {
        System.out.println("宗门任务定时任务执行啦！");
        Iterator var1 = BotFactory.getBots().values().iterator();

        while(var1.hasNext()) {
            Bot bot = (Bot)var1.next();
            BotConfig botConfig = bot.getBotConfig();
            long groupId = botConfig.getGroupId();
            if (botConfig.getTaskId() != 0L) {
                groupId = botConfig.getTaskId();
            }
            if (xbGroupId != 0){
                groupId = xbGroupId;
            }

            Group group = bot.getGroup(groupId);

            try {
                group.sendMessage((new MessageChain()).at("3889029313").text("宗门丹药领取"));
                Thread.sleep(2000L);
                group.sendMessage((new MessageChain()).text("开始小北自动宗门任务"));
            } catch (Exception var8) {
            }
        }

    }
}
