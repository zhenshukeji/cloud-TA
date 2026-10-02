package com.zhenshu.document;

import org.apache.commons.io.FileUtils;
import org.xmind.core.*;

import java.io.File;
import java.io.IOException;
import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jing
 * @version 1.0
 * @desc 生成代码结构
 * @date 2021/9/26 0026 18:51
 **/
public class DocumentStructure {

    public static ArrayList<ITopic> chapterTopics = new ArrayList<>();

    public static void main(String[] args) throws CoreException, IOException {
        String root = "F:\\code\\JAVA\\JavaHome\\SpringBootTemplate\\src\\main\\java";
        String xmindPath = "./template.xmind";
        createXmind(root, xmindPath);
    }

    public static void traverseFolder2(String path, ITopic topic, IWorkbook workbook) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        ITopic folderTopic = workbook.createTopic();
                        folderTopic.setTitleText(file2.getName());
                        topic.add(folderTopic);
                        traverseFolder2(file2.getAbsolutePath(), folderTopic, workbook);
                    } else {
                        ITopic fileTopic = workbook.createTopic();
                        fileTopic.setTitleText(file2.getName());
                        if (file2.getName().contains(".xml")) {
                            continue;
                        }
                        topic.add(fileTopic);
                        IPlainNotesContent plainContent = (IPlainNotesContent) workbook.createNotesContent(INotes.PLAIN);

                        List<String> strings = FileUtils.readLines(file2, "utf-8");
                        for (String line : strings) {
                            if (line.contains("@desc")) {
                                plainContent.setTextContent(line.replaceAll("@desc", "").replaceAll("\\*", ""));
                                break;
                            }

                            String[] filterArr = {
                                    "/", "@", "import", "<p>", "     *", "this", "int", "public", "\t *", "String", "char", "new", "Copyright"
                            };
                            if (line.contains("*")) {
                                boolean result = true;
                                for (String s : filterArr) {
                                    if (line.contains(s)) {
                                        result = false;
                                        break;
                                    }
                                }
                                if (result) {
                                    System.out.println(line);
                                    plainContent.setTextContent(line.replaceAll("\\*", ""));
                                    break;
                                }
                            }
                        }
                        INotes notes = fileTopic.getNotes();
                        notes.setContent(INotes.PLAIN, plainContent);
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
    }


    public static void createXmind(String root, String path) throws CoreException, IOException {
        // 创建思维导图的工作空间
        IWorkbookBuilder workbookBuilder = Core.getWorkbookBuilder();
        IWorkbook workbook = workbookBuilder.createWorkbook();
        // 获得默认sheet
        ISheet primarySheet = workbook.getPrimarySheet();
        // 获得根主题
        ITopic rootTopic = primarySheet.getRootTopic();
        // 设置根主题的标题
        rootTopic.setTitleText(root);
        //正确的逻辑图 org.xmind.ui.logic.right
        rootTopic.setStructureClass("org.xmind.ui.logic.right");
        ITopic topic = workbook.createTopic();
        traverseFolder2(root, topic, workbook);
        chapterTopics.add(topic);
        //把所有一级节点都加到根节点上
        chapterTopics.forEach(it -> rootTopic.add(it, ITopic.ATTACHED));
        workbook.save(path);
    }
}
