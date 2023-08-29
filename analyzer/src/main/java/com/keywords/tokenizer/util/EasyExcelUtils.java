package com.keywords.tokenizer.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther zhangqingjie
 * @Date 2023/3/3
 */
@Slf4j
public class EasyExcelUtils {

    //-------------------------------------------------------------- 读取文件解析监听类 start ----------------------------------------------------

    /**
     * ClassName：ExcelListener
     * Description：读取文件解析监听类，此类供外部实例化使用需要设置为静态类
     */
    public static class ExcelListener<T> extends AnalysisEventListener<T> {

        /**
         * <p>存放读取后的数据</p >
         *
         * @date 2021/9/2 0:10
         */
        public List<T> datas = new ArrayList<>();

        /**
         * <p>读取数据，一条一条读取</p >
         *
         * @date 2021/9/2 0:15
         */
        @Override
        public void invoke(T t, AnalysisContext analysisContext) {
            datas.add(t);
        }

        /**
         * <p>解析完毕之后执行</p >
         *
         * @date 2021/9/2 0:06
         */
        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            log.info("读取数据条数：{}条！", datas.size());
        }

        public List<T> getDatas() {
            return this.datas;
        }

    }
    //-------------------------------------------------------------- 读取文件解析监听类 end ----------------------------------------------------


    //-------------------------------------------------------------- 导出excel表格，设置自适应列宽配置类 start ----------------------------------------------------

    /**
     * ClassName：CustomHandler
     * Description：设置自适应列宽配置类
     */
    public static class CustomHandler extends AbstractColumnWidthStyleStrategy {

        private static final int MAX_COLUMN_WIDTH = 255;
        //因为在自动列宽的过程中，有些设置地方让列宽显得紧凑，所以做出了个判断
        private static final int COLUMN_WIDTH = 20;
        private Map<Integer, Map<Integer, Integer>> CACHE = new HashMap(8);

        @Override
        protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
            boolean needSetWidth = isHead || !CollectionUtils.isEmpty(cellDataList);
            if (needSetWidth) {
                Map<Integer, Integer> maxColumnWidthMap = (Map) CACHE.get(writeSheetHolder.getSheetNo());
                if (maxColumnWidthMap == null) {
                    maxColumnWidthMap = new HashMap(16);
                    CACHE.put(writeSheetHolder.getSheetNo(), maxColumnWidthMap);
                }

                Integer columnWidth = this.dataLength(cellDataList, cell, isHead);
                if (columnWidth >= 0) {
                    if (columnWidth > MAX_COLUMN_WIDTH) {
                        columnWidth = MAX_COLUMN_WIDTH;
                    } else {
                        if (columnWidth < COLUMN_WIDTH) {
                            columnWidth = columnWidth * 2;
                        }
                    }

                    Integer maxColumnWidth = (Integer) ((Map) maxColumnWidthMap).get(cell.getColumnIndex());
                    if (maxColumnWidth == null || columnWidth > maxColumnWidth) {
                        ((Map) maxColumnWidthMap).put(cell.getColumnIndex(), columnWidth);
                        writeSheetHolder.getSheet().setColumnWidth(cell.getColumnIndex(), columnWidth * 256);
                    }
                }
            }
        }


        private Integer dataLength(List<WriteCellData<?>> cellDataList, Cell cell, Boolean isHead) {
            if (isHead) {
                return cell.getStringCellValue().getBytes().length;
            } else {
                CellData cellData = (CellData) cellDataList.get(0);
                CellDataTypeEnum type = cellData.getType();
                if (type == null) {
                    return -1;
                } else {
                    switch (type) {
                        case STRING:
                            return cellData.getStringValue().getBytes().length;
                        case BOOLEAN:
                            return cellData.getBooleanValue().toString().getBytes().length;
                        case NUMBER:
                            return cellData.getNumberValue().toString().getBytes().length;
                        default:
                            return -1;
                    }
                }
            }
        }
    }
    //-------------------------------------------------------------- 导出excel表格，设置自适应列宽配置类 end -----------------------------------------------------

    /**
     * 写入本地Excel文件
     *
     * @param tClass 输出格式
     * @param datas  输出的数据
     * @return：
     */
    public static <T> void writeEasyExcel(Class<T> tClass, List<T> datas, String fileName) {

        // 表头样式
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 设置表头居中对齐
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 内容样式
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 设置内容居中对齐
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);

        WriteSheet sheet = EasyExcel.writerSheet("sheet").build();

        EasyExcel.write(fileName, tClass)
                .registerWriteHandler(new CustomHandler())
                .registerWriteHandler(horizontalCellStyleStrategy)
                .excelType(ExcelTypeEnum.XLSX)
                .build()
                .write(datas, sheet)
                .finish();
    }

}
