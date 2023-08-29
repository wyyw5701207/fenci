package com.keywords.tokenizer.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author wangyue
 * @date 2023/8/11
 */
public class FileWriteUtil {
    public static<T> void write(Class<T> tClass, List<T> list) throws IOException {
        String fileName = "结果导出-" + DateUtils.formatDate(new Date()) + ".xlsx";
        File f=new File(fileName);
        f.createNewFile();
        // 输出结果
        EasyExcelUtils.writeEasyExcel(tClass, list,fileName);
    }
}
