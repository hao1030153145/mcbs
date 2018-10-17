package com.transing.workflow.web.controller;

import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Administrator on 2017/8/17 0017.
 */
public class VisualizationControllerTest extends AbstractSpringBaseControllerTest {
    @Test
    @Rollback(value = false)
    public void getVisTemplate() throws Exception{
        String requestURI = "/visualization/getVisTemplate.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void getVisList() throws Exception{
        String requestURI = "/visualization/getVisList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId","427")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void getVisModuleList() throws Exception{
        String requestURI = "/visualization/getVisModuleList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("visId","81")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void getVisData() throws Exception{
        String requestURI = "/visualization/getVisData.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("jsonParam", "{\"visChartId\":1,\"sortOrder\":[{\"field\":\"themeJSON@领域\",\"order\":\"asc\"}],\"dataResultId\":1,\"paramId\":2190,\"xAxis\":\"themeJSON@领域\",\"yAxis\":\"title@Statistics\",\"k\":\"themeJSON@领域\",\"series\":\"splitResult\",\"limitNum\":\"\"}")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void saveVis() throws Exception{
        String requestURI = "/visualization/saveVis.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("name","张三").param("tepmId","1212").param("projectId","1314")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void getImage() throws Exception{
        String requestURI = "/visualization/getImage.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.get(requestURI).param("testInLogin", "no").param("image","D:\\data\\project\\datacrawl\\tmp\\visId1503303722703.png")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void saveVisInfo() throws Exception{
        String requestURI = "/visualization/saveVisInfo.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("visId","1").param("settingsArray","[\n" +
                        "    {\n" +
                        "        \"key\": 1503297628387,\n" +
                        "        \"settings\": {\n" +
                        "            \"position\": \"1\",\n" +
                        "            \"id\": 1,\n" +
                        "            \"type\": \"chart\",\n" +
                        "            \"paramSettings\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"颜色设置\",\n" +
                        "                    \"type\": \"table\",\n" +
                        "                    \"isShow\": false,\n" +
                        "                    \"params\": [\n" +
                        "                        {\n" +
                        "                            \"set\": \"from\",\n" +
                        "                            \"name\": \"从\",\n" +
                        "                            \"value\": \"\"\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"set\": \"to\",\n" +
                        "                            \"name\": \"到\",\n" +
                        "                            \"value\": \"\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"文本属性\",\n" +
                        "                    \"isShow\": false,\n" +
                        "                    \"type\": \"height\",\n" +
                        "                    \"params\": [\n" +
                        "                        {\n" +
                        "                            \"set\": \"height\",\n" +
                        "                            \"name\": \"高度\",\n" +
                        "                            \"value\": \"\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"表格\",\n" +
                        "                    \"type\": \"desc\",\n" +
                        "                    \"isShow\": true,\n" +
                        "                    \"params\": [\n" +
                        "                        {\n" +
                        "                            \"set\": \"xAxis.name\",\n" +
                        "                            \"name\": \"x轴\",\n" +
                        "                            \"type\": \"text\",\n" +
                        "                            \"value\": \"34\"\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"set\": \"yAxis.name\",\n" +
                        "                            \"name\": \"y轴\",\n" +
                        "                            \"type\": \"text\",\n" +
                        "                            \"value\": \"234\"\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"set\": \"title.text\",\n" +
                        "                            \"name\": \"标题\",\n" +
                        "                            \"type\": \"text\",\n" +
                        "                            \"value\": \"23\"\n" +
                        "                        },\n" +
                        "                        {\n" +
                        "                            \"set\": \"title.subtext\",\n" +
                        "                            \"name\": \"描述\",\n" +
                        "                            \"type\": \"text\",\n" +
                        "                            \"value\": \"\"\n" +
                        "                        }\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        \"tableData\": [\n" +
                        "            [\n" +
                        "                \"site\",\n" +
                        "                \"url\",\n" +
                        "                \"url\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"36氪\",\n" +
                        "                \"5\",\n" +
                        "                \"5\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"网易汽车\",\n" +
                        "                \"6\",\n" +
                        "                \"6\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"微信\",\n" +
                        "                \"4\",\n" +
                        "                \"4\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"36氪\",\n" +
                        "                \"8\",\n" +
                        "                \"8\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"网易汽车\",\n" +
                        "                \"6\",\n" +
                        "                \"6\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"微信\",\n" +
                        "                \"4\",\n" +
                        "                \"4\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"36氪\",\n" +
                        "                \"7\",\n" +
                        "                \"7\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"网易汽车\",\n" +
                        "                \"6\",\n" +
                        "                \"6\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"微信\",\n" +
                        "                \"7\",\n" +
                        "                \"7\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"36氪\",\n" +
                        "                \"4\",\n" +
                        "                \"4\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"网易汽车\",\n" +
                        "                \"5\",\n" +
                        "                \"5\"\n" +
                        "            ],\n" +
                        "            [\n" +
                        "                \"微信\",\n" +
                        "                \"6\",\n" +
                        "                \"6\"\n" +
                        "            ]\n" +
                        "        ],\n" +
                        "        \"data\": {\n" +
                        "            \"title\": {\n" +
                        "                \"text\": \"23\",\n" +
                        "                \"subtext\": \"数据\"\n" +
                        "            },\n" +
                        "            \"tooltip\": {\n" +
                        "                \"trigger\": \"axis\"\n" +
                        "            },\n" +
                        "            \"legend\": {\n" +
                        "                \"data\": [\n" +
                        "                    \"36氪\",\n" +
                        "                    \"网易汽车\",\n" +
                        "                    \"微信\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"toolbox\": {\n" +
                        "                \"show\": true,\n" +
                        "                \"feature\": {\n" +
                        "                    \"mark\": {\n" +
                        "                        \"show\": true\n" +
                        "                    },\n" +
                        "                    \"magicType\": {\n" +
                        "                        \"show\": false,\n" +
                        "                        \"type\": [\n" +
                        "                            \"line\",\n" +
                        "                            \"bar\"\n" +
                        "                        ]\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"calculable\": true,\n" +
                        "            \"xAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"category\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"5\",\n" +
                        "                        \"6\",\n" +
                        "                        \"7\",\n" +
                        "                        \"8\"\n" +
                        "                    ],\n" +
                        "                    \"boundaryGap\": false,\n" +
                        "                    \"name\": \"34\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"yAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"value\",\n" +
                        "                    \"name\": \"234\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"series\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"36氪\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"5\",\n" +
                        "                        \"8\",\n" +
                        "                        \"7\",\n" +
                        "                        \"4\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"网易汽车\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"5\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"微信\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"4\",\n" +
                        "                        \"7\",\n" +
                        "                        \"6\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        },\n" +
                        "        \"inputParam\": [\n" +
                        "            {\n" +
                        "                \"field\": \"xAxis\",\n" +
                        "                \"fieldName\": \"X轴\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"field\": \"yAxis\",\n" +
                        "                \"fieldName\": \"y轴\"\n" +
                        "            },\n" +
                        "            {\n" +
                        "                \"field\": \"series\",\n" +
                        "                \"fieldName\": \"图例\"\n" +
                        "            }\n" +
                        "        ],\n" +
                        "        \"visChartId\": 1,\n" +
                        "        \"inputSettingsParam\": \"{\\\"visChartId\\\":1,\\\"paramId\\\":1,\\\"dataResultId\\\":1,\\\"xAxis\\\":\\\"url\\\",\\\"yAxis\\\":\\\"url\\\",\\\"series\\\":\\\"site\\\"}\"\n" +
                        "    }\n" +
                        "]").param("chartsArray","[\n" +
                        "    {\n" +
                        "        \"key\": 1503297628387,\n" +
                        "        \"type\": \"chart\",\n" +
                        "        \"option\": {\n" +
                        "            \"title\": {\n" +
                        "                \"text\": \"23\",\n" +
                        "                \"subtext\": \"数据\"\n" +
                        "            },\n" +
                        "            \"tooltip\": {\n" +
                        "                \"trigger\": \"axis\"\n" +
                        "            },\n" +
                        "            \"legend\": {\n" +
                        "                \"data\": [\n" +
                        "                    \"36氪\",\n" +
                        "                    \"网易汽车\",\n" +
                        "                    \"微信\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"toolbox\": {\n" +
                        "                \"show\": true,\n" +
                        "                \"feature\": {\n" +
                        "                    \"mark\": {\n" +
                        "                        \"show\": true\n" +
                        "                    },\n" +
                        "                    \"magicType\": {\n" +
                        "                        \"show\": false,\n" +
                        "                        \"type\": [\n" +
                        "                            \"line\",\n" +
                        "                            \"bar\"\n" +
                        "                        ]\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"calculable\": true,\n" +
                        "            \"xAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"category\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"5\",\n" +
                        "                        \"6\",\n" +
                        "                        \"7\",\n" +
                        "                        \"8\"\n" +
                        "                    ],\n" +
                        "                    \"boundaryGap\": false,\n" +
                        "                    \"name\": \"34\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"yAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"value\",\n" +
                        "                    \"name\": \"234\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"series\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"36氪\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"5\",\n" +
                        "                        \"8\",\n" +
                        "                        \"7\",\n" +
                        "                        \"4\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"网易汽车\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"5\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"微信\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"4\",\n" +
                        "                        \"7\",\n" +
                        "                        \"6\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"key\": 1503297628387,\n" +
                        "        \"type\": \"chart\",\n" +
                        "        \"option\": {\n" +
                        "            \"title\": {\n" +
                        "                \"text\": \"统计分析\",\n" +
                        "                \"subtext\": \"数据\"\n" +
                        "            },\n" +
                        "            \"tooltip\": {\n" +
                        "                \"trigger\": \"axis\"\n" +
                        "            },\n" +
                        "            \"legend\": {\n" +
                        "                \"data\": [\n" +
                        "                    \"36氪\",\n" +
                        "                    \"网易汽车\",\n" +
                        "                    \"微信\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"toolbox\": {\n" +
                        "                \"show\": true,\n" +
                        "                \"feature\": {\n" +
                        "                    \"mark\": {\n" +
                        "                        \"show\": true\n" +
                        "                    },\n" +
                        "                    \"magicType\": {\n" +
                        "                        \"show\": false,\n" +
                        "                        \"type\": [\n" +
                        "                            \"line\",\n" +
                        "                            \"bar\"\n" +
                        "                        ]\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"calculable\": true,\n" +
                        "            \"xAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"category\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"5\",\n" +
                        "                        \"6\",\n" +
                        "                        \"7\",\n" +
                        "                        \"8\"\n" +
                        "                    ],\n" +
                        "                    \"boundaryGap\": false\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"yAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"value\",\n" +
                        "                    \"name\": \"\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"series\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"36氪\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"2017/6/21\",\n" +
                        "                        \"2017/6/22\",\n" +
                        "                        \"2017/6/23\",\n" +
                        "                        \"2017/6/24\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"网易汽车\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"2017/6/21\",\n" +
                        "                        \"2017/6/22\",\n" +
                        "                        \"2017/6/23\",\n" +
                        "                        \"2017/6/24\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"微信\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"2017/6/21\",\n" +
                        "                        \"2017/6/22\",\n" +
                        "                        \"2017/6/23\",\n" +
                        "                        \"2017/6/24\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"key\": 1503297628387,\n" +
                        "        \"type\": \"chart\",\n" +
                        "        \"option\": {\n" +
                        "            \"title\": {\n" +
                        "                \"text\": \"统计分析\",\n" +
                        "                \"subtext\": \"数据\"\n" +
                        "            },\n" +
                        "            \"tooltip\": {\n" +
                        "                \"trigger\": \"axis\"\n" +
                        "            },\n" +
                        "            \"legend\": {\n" +
                        "                \"data\": [\n" +
                        "                    \"36氪\",\n" +
                        "                    \"网易汽车\",\n" +
                        "                    \"微信\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"toolbox\": {\n" +
                        "                \"show\": true,\n" +
                        "                \"feature\": {\n" +
                        "                    \"mark\": {\n" +
                        "                        \"show\": true\n" +
                        "                    },\n" +
                        "                    \"magicType\": {\n" +
                        "                        \"show\": false,\n" +
                        "                        \"type\": [\n" +
                        "                            \"line\",\n" +
                        "                            \"bar\"\n" +
                        "                        ]\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"calculable\": true,\n" +
                        "            \"xAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"category\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"2017/6/21\",\n" +
                        "                        \"2017/6/23\",\n" +
                        "                        \"2017/6/22\",\n" +
                        "                        \"2017/6/24\"\n" +
                        "                    ],\n" +
                        "                    \"boundaryGap\": false\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"yAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"value\",\n" +
                        "                    \"name\": \"\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"series\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"36氪\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"5\",\n" +
                        "                        \"8\",\n" +
                        "                        \"7\",\n" +
                        "                        \"4\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"网易汽车\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"5\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"微信\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"4\",\n" +
                        "                        \"7\",\n" +
                        "                        \"6\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"key\": 1503297628387,\n" +
                        "        \"type\": \"chart\",\n" +
                        "        \"option\": {\n" +
                        "            \"title\": {\n" +
                        "                \"text\": \"统计分析\",\n" +
                        "                \"subtext\": \"数据\"\n" +
                        "            },\n" +
                        "            \"tooltip\": {\n" +
                        "                \"trigger\": \"axis\"\n" +
                        "            },\n" +
                        "            \"legend\": {\n" +
                        "                \"data\": [\n" +
                        "                    \"36氪\",\n" +
                        "                    \"网易汽车\",\n" +
                        "                    \"微信\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"toolbox\": {\n" +
                        "                \"show\": true,\n" +
                        "                \"feature\": {\n" +
                        "                    \"mark\": {\n" +
                        "                        \"show\": true\n" +
                        "                    },\n" +
                        "                    \"magicType\": {\n" +
                        "                        \"show\": false,\n" +
                        "                        \"type\": [\n" +
                        "                            \"line\",\n" +
                        "                            \"bar\"\n" +
                        "                        ]\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"calculable\": true,\n" +
                        "            \"xAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"category\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"2017/6/21\",\n" +
                        "                        \"2017/6/23\",\n" +
                        "                        \"2017/6/22\",\n" +
                        "                        \"2017/6/24\"\n" +
                        "                    ],\n" +
                        "                    \"boundaryGap\": false\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"yAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"value\",\n" +
                        "                    \"name\": \"\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"series\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"36氪\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"5\",\n" +
                        "                        \"8\",\n" +
                        "                        \"7\",\n" +
                        "                        \"4\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"网易汽车\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"5\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"微信\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"4\",\n" +
                        "                        \"7\",\n" +
                        "                        \"6\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    },\n" +
                        "    {\n" +
                        "        \"key\": 1503297628387,\n" +
                        "        \"type\": \"chart\",\n" +
                        "        \"option\": {\n" +
                        "            \"title\": {\n" +
                        "                \"text\": \"23\",\n" +
                        "                \"subtext\": \"数据\"\n" +
                        "            },\n" +
                        "            \"tooltip\": {\n" +
                        "                \"trigger\": \"axis\"\n" +
                        "            },\n" +
                        "            \"legend\": {\n" +
                        "                \"data\": [\n" +
                        "                    \"36氪\",\n" +
                        "                    \"网易汽车\",\n" +
                        "                    \"微信\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"toolbox\": {\n" +
                        "                \"show\": true,\n" +
                        "                \"feature\": {\n" +
                        "                    \"mark\": {\n" +
                        "                        \"show\": true\n" +
                        "                    },\n" +
                        "                    \"magicType\": {\n" +
                        "                        \"show\": false,\n" +
                        "                        \"type\": [\n" +
                        "                            \"line\",\n" +
                        "                            \"bar\"\n" +
                        "                        ]\n" +
                        "                    }\n" +
                        "                }\n" +
                        "            },\n" +
                        "            \"calculable\": true,\n" +
                        "            \"xAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"category\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"5\",\n" +
                        "                        \"6\",\n" +
                        "                        \"7\",\n" +
                        "                        \"8\"\n" +
                        "                    ],\n" +
                        "                    \"boundaryGap\": false,\n" +
                        "                    \"name\": \"34\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"yAxis\": [\n" +
                        "                {\n" +
                        "                    \"type\": \"value\",\n" +
                        "                    \"name\": \"234\"\n" +
                        "                }\n" +
                        "            ],\n" +
                        "            \"series\": [\n" +
                        "                {\n" +
                        "                    \"name\": \"36氪\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"5\",\n" +
                        "                        \"8\",\n" +
                        "                        \"7\",\n" +
                        "                        \"4\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"网易汽车\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"6\",\n" +
                        "                        \"5\"\n" +
                        "                    ]\n" +
                        "                },\n" +
                        "                {\n" +
                        "                    \"name\": \"微信\",\n" +
                        "                    \"type\": \"line\",\n" +
                        "                    \"data\": [\n" +
                        "                        \"4\",\n" +
                        "                        \"4\",\n" +
                        "                        \"7\",\n" +
                        "                        \"6\"\n" +
                        "                    ]\n" +
                        "                }\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    }\n" +
                        "]").param("image","data:image/png;base64,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")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }
}
