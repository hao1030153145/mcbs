package com.transing.workflow.util;

import com.jeeframework.util.validate.Validate;
import com.transing.dpmbs.util.ExcelUtil;
import com.transing.dpmbs.util.WebUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * 描述
 *
 * @author lance
 * @version 1.0 2017-02-26 21:01
 */
public class ExcelUtilTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void combinateStaticURL() throws Exception {
//        List<String[]> list = ExcelUtil.readExcel("D:\\document\\excel\\第一财经_财经650 - 2003.xls");
        int num  =  ExcelUtil.readExcelRowNum("D:\\document\\excel\\新浪行业-国产新车（Alex）500车市.xlsx");
        System.out.println(num);
    }

}