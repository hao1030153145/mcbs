package com.transing.workflow.web.controller;


import com.jeeframework.logicframework.util.logging.LoggerUtil;
import com.jeeframework.testframework.AbstractSpringBaseControllerTest;
import com.jeeframework.util.encrypt.BASE64Util;
import com.jeeframework.util.encrypt.Base64;
import com.jeeframework.util.httpclient.HttpClientHelper;
import com.jeeframework.util.httpclient.HttpResponse;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpException;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImportDataControllerTest extends AbstractSpringBaseControllerTest {
    public static final String APBS_URL = "localhost:8080"; //apbs的http访问地址
    public static final String HTTP_PROTOCOL = "http://"; //http访问地址前缀
    public static final String GET_TERM_LIST_URI = "/importData/getUploadFile.json"; //访问词库的列表接口

    private static String EXCEL = "UEsDBAoAAAAAAIdO4kAAAAAAAAAAAAAAAAAJAAAAZG9jUHJvcHMvUEsDBBQAAAAIAIdO4kC7N9mv" +
            "MAEAADQCAAAQAAAAZG9jUHJvcHMvYXBwLnhtbJ2RwUoDMRRF94L/ELJvMy0iUjIpgog7B1p1HTNv" +
            "2sBMEpLn0PotLnQh+Adu9G9U/AwzE9CpuHJ3X+7lvvMIn2+amrTgg7Ymp5NxRgkYZUttVjm9WJ6O" +
            "jigJKE0pa2sgp1sIdC7293jhrQOPGgKJFSbkdI3oZowFtYZGhnG0TXQq6xuJcfQrZqtKKzix6qYB" +
            "g2yaZYcMNgimhHLkvgtpapy1+N/S0qqOL1wuty4CC37sXK2VxHiluCoW5PPh6eP+hbPhOz8D2d1d" +
            "SO2D4C3OWlBoPQn6Nl4+peRaBugac9pKr6XB2NzF0tDr2gX04v358e31Li7hLPrprZfD6FDrAzHp" +
            "A1HsBruCxBGNXcKlxhrCeVVIj38AT4bAPUPCTTiLNQCmnUO+/uK46Vc3+/lu8QVQSwMEFAAAAAgA" +
            "h07iQKkNJWExAQAANwIAABEAAABkb2NQcm9wcy9jb3JlLnhtbI2RwU7DMBBE70j8Q+R7YrstpbKS" +
            "VAjUE0hIBIG4Wfa2tYgdyzak/XucpA1BcOC4ntm3s958fdB18gnOq8YUiGYEJWBEI5XZFei52qQr" +
            "lPjAjeR1Y6BAR/BoXV5e5MIy0Th4dI0FFxT4JJKMZ8IWaB+CZRh7sQfNfRYdJorbxmkeYul22HLx" +
            "zneAZ4QssYbAJQ8cd8DUjkR0QkoxIu2Hq3uAFBhq0GCCxzSj+NsbwGn/Z0OvTJxahaONO53iTtlS" +
            "DOLoPng1Gtu2zdp5HyPmp/j14f6pXzVVpvsrAajMpWDCAQ+NK2+kVkb54LoqxxMluvpQgxVkEsew" +
            "IdRZeZnf3lUbVM4IvU4JTWfziqzY1YIR8taxfvR3Y4cHHU+4Vf8jLipKGFkyspgQz4Ayx79OXX4B" +
            "UEsDBBQAAAAIAIdO4kCKgsmS/gAAAH8BAAATAAAAZG9jUHJvcHMvY3VzdG9tLnhtbJ3QT0vDMBgG" +
            "8Lvgdwi5p0kzOteSdth2u3hQcO5e0nQLNH9I0moRv7sZ03n3+PK8/Hjel20/1Ahm4bw0uoRpQiAQ" +
            "mpte6lMJ3w57tIHAh0733Wi0KOEiPNxW93fsxRkrXJDCg0hoX8JzCLbA2POzUJ1PYqxjMhinuhBH" +
            "d8JmGCQXreGTEjpgSsga88kHo5C9cfDqFXP4L9kbfmnnj4fFxroV+8EXMKgg+xJ+tlnTthnJEN3l" +
            "DUpJWqN8lT8gsiGE1rTZ54+7LwjsZZlCoDsVT396fY5sP/FQT3Lsj8JFeg7FaN99cBUlGY1UEn+Y" +
            "rNNVxvBfxvBvhYrhS7fr56pvUEsDBAoAAAAAAIdO4kAAAAAAAAAAAAAAAAADAAAAeGwvUEsDBAoA" +
            "AAAAAIdO4kAAAAAAAAAAAAAAAAAOAAAAeGwvd29ya3NoZWV0cy9QSwMEFAAAAAgAh07iQGUv0SVq" +
            "BAAAWQ4AABgAAAB4bC93b3Jrc2hlZXRzL3NoZWV0MS54bWyVl9tu4zYQhu8L9B0E3a9FijoGthfr" +
            "g9ACLbBod9trRaZjIZLoSkycvH2HpEmR9LaIA8SyzW9O/wwlevn5re+CVzpOLRtWIV6gMKBDww7t" +
            "8LQKv3+rPhVhMPF6ONQdG+gqfKdT+Hn980/LCxufpxOlPAAPw7QKT5yfH6Joak60r6cFO9MBVo5s" +
            "7GsOH8enaDqPtD5Io76LYoSyqK/bIVQeHsaP+GDHY9vQHWteejpw5WSkXc0h/+nUnift7e3wIX+H" +
            "sb5ArTofK8WdWjH+cHKTX982I5vYkS8a1kcqtdsqy6h06uybG0c/EKuvx+eX8ydwfIbiHtuu5e+y" +
            "3HC9lBJ+HaP18tCCDKJ3wUiPq/ALfqgwCmFBIn+19DJZ7wNeP/5JO9pweoBmh4Fo4iNjzwL8Fb5C" +
            "wrkEhMu64e0r3dKuW4XbDObgHxkE3kKAyESw3+tolWz71zE40GP90vEt6/5uD/y0CstQf/cHu/xC" +
            "26cTh1SyRRoG7IV37UB/o6+0A4NVmIpADevAK7wGfStGNAz6+k1lrzxmCxyD9cTfOxhQWG9eJs76" +
            "azx89aGs46s1XC8mH2OpoimSXEm4XklMFvlH4yRXa7hq63hROOZ2MHAri4LrFY/xgjj4/1UFvZHm" +
            "cJ3NPy5KfjXHGSnmhKFRtjCR6oPs+q7m9Xo5sksAewwEFy1MQCglvGkqODjX4taAHzAMViPoL4DD" +
            "N6A/2Ezw7esaLaNXmKEG/sGl8QuI8EtAk//2mxm/gINfSML4xcavjLy5JWKX2N4SxCV2t0TiEvtb" +
            "InWJ6pbIDOEoAMUIBWLoyUcUAFwqIDT1a9drszq5iSnV2WpCWpMsK3CWFsSjdjZFUEzyLE8TD9pr" +
            "aA5WuMEqRUBdplmlIRwJALlHAsCNBF5zN3ptjonnyVMaaERqUOQFSrIySb3ydjZVZAnKixh7wfaa" +
            "sYJ5LakUAq9GA8uLIwLsgHtEANyI4M3vRq9ZQT1kqxEpQloUBJE4vRHBpsqkLOI0TzxPe81Ywbzd" +
            "UinEEWHeLo4IcG+7RwTxuBK3A1GFF3Sj16y85h2oJkEjahLiJIkLnBKvyzubSgkuUxiXeZClp71m" +
            "rGDeQFUKcUSYN4wjAtyp7xEBcCPCLKvMa6PXrLy81LcaUZOAUJHkhOS+CDaVoZKkeZ57cu41MweL" +
            "vb1XKcQWIZ53jCMCnEjvEUEcYPUkeHlt9JqVl1feViPXSUAowVDinJpUc2dTJC9RkaLMq3CvGSuY" +
            "t2MqhTgizMPriABP53tEEA9zLYI3fhu9ZuXlDctWI0qEskBpked+C3c2Fcc5wnmWZd5Q7TVkRfO6" +
            "UinEUWHO2VEBzrp3ySB4o8O8x9SOMItWah6zNYxSghRxTGKSedjOweIc5yhF3sjsDWOF88QSZ3mR" +
            "ry0FmcdKSaEO4upIdq6f6O/1+NQOU9DRIzxZkDyzjuqYrT5wdpantkfG4ZisDnDwu4zC6QwtUoyh" +
            "u/oP7kxHxviPl+AAe5J2lUTgY2R+FK7/BVBLAwQKAAAAAACHTuJAAAAAAAAAAAAAAAAACQAAAHhs" +
            "L3RoZW1lL1BLAwQUAAAACACHTuJA6ibPn+cFAAA2GQAAEwAAAHhsL3RoZW1lL3RoZW1lMS54bWzt" +
            "WU1vGzcQvRfof1jsvZFk6yMyIge2PuImdhJESoocqV1qlxF3uSApO7oVybFAgaJp0UuB3noo2gZI" +
            "gF7SX+M2RZsC+QsdclcrUqJqx8ghLWJfJO6b4ePM8A25unL1YUK9Y8wFYWnHr12q+h5OAxaSNOr4" +
            "d0eDjy77npAoDRFlKe74cyz8q7sffnAF7cgYJ9gD+1TsoI4fS5ntVCoigGEkLrEMp/BswniCJHzl" +
            "USXk6AT8JrSyVa02Kwkiqe+lKAG3tyYTEmB/d+G2T8F3KoUaCCgfKqd4HRtOawoh5qJLuXeMaMeH" +
            "GUJ2MsIPpe9RJCQ86PhV/edXdq9U0E5hROUGW8NuoP8Ku8IgnG7pOXk0Liet1xv15l7pXwOoXMf1" +
            "W/1mv1n60wAUBLDSnIvps7Hf3u81CqwByj86fPdave2ahTf8b69x3muofwuvQbn/+hp+MOhCFC28" +
            "BuX4xhq+Xm9tdesWXoNyfHMN36ru9eotC69BMSXpdA1dbTS3u4vVlpAJowdOeLtRH7S2CudLFFRD" +
            "WV1qiglL5aZaS9ADxgcAUECKJEk9Oc/wBAVQv11EyZgT75BEsVTToB2MjOf5UCDWhtSMngg4yWTH" +
            "v54h2BFLr69f/Pj6xTPv9Yunp4+enz765fTx49NHP+e+LMMDlEam4avvv/j720+9v5599+rJV268" +
            "MPG///TZb79+6QbCPloyevn10z+eP335zed//vDEAd/jaGzCRyTBwruJT7w7LIG16cDYzPGYv5nF" +
            "KEbEskAx+Ha47svYAt6cI+rC7WM7ePc4SIgLeG32wOI6jPlMEsfMN+LEAh4xRvcZdwbghprLiPBo" +
            "lkbuyfnMxN1B6Ng1dxelVmr7swy0k7hcdmNs0bxNUSpRhFMsPfWMTTF2rO4+IVZcj0jAmWAT6d0n" +
            "3j4izpCMyNgqpKXRAUkgL3MXQUi1FZuje94+o65V9/CxjYQNgaiD/AhTK4zX0EyixOVyhBJqBvwQ" +
            "ydhFcjjngYnrCwmZjjBlXj/EQrhsbnFYr5H0GyAf7rQf0XliI7kkU5fPQ8SYieyxaTdGSebCDkka" +
            "m9iPxRRKFHm3mXTBj5i9Q9R3yANKN6b7HsFWus8WgrugnCalZYGoJzPuyOU1zKz6Hc7pBGGtMiDs" +
            "ll4nJD1TvPMZ3st2x9/jxLl5DlbEehPuPyjRPTRLb2PYFest6r1Cv1do/3+v0Jv28tvX5aUUg0qr" +
            "w2B+4tbn72Tj8XtCKB3KOcWHQp/ABTSgcACDyk5fOnF5Hcti+Kh2Mkxg4SKOtI3HmfyEyHgYowxO" +
            "7zVfOYlE4ToSXsYE3Br1sNO3wtNZcsTC/NZZq6kbZi4eAsnleLVRjsONQeboZmt5kyrda7aRvvEu" +
            "CCjbNyFhTGaT2HaQaC0GVZD0/RqC5iChV/ZWWLQdLC4r94tUrbEAamVW4ITkwbmq4zfqYAJGcG1C" +
            "FIcqT3mqF9nVyXybmd4UTKsCqvBSo6iAZabbiuvG5anV5aV2jkxbJIxys0noyOgeJmIU4qI61eh5" +
            "aLxprtvLlFr0VCiKWBg0Wpf/jcVFcw12q9pAU1MpaOqddPzmdgNKJkBZx5/A7R0+JhnUjlAnW0Qj" +
            "ePkVSJ5v+IsoS8aF7CER5wHXopOrQUIk5h4lScdXyy/TQFOtIZpbbQsE4Z0l1wZZedfIQdLtJOPJ" +
            "BAfSTLsxoiKdfwWFz7XC+VSbXxysLNkM0j2MwxNvTGf8DoISa7RqKoAhEfCKp5ZHMyTwVrIUsmX9" +
            "rTSmQnbN14K6hvJxRLMYFR3FFPMcrqW8pKO/lTEwvhVrhoAaISka4ThSDdYMqtVNy66Rc9jYdc82" +
            "UpEzRHPZMy1VUV3TrWLWDIs2sBLLizV5g9UixNAuzQ6fS/eq5LYXWrdyTii7BAS8jJ+j656jIRjU" +
            "lpNZ1BTjdRlWml2M2r1jscAzqJ2nSRiq31y4XYlb2SOc08HghTo/2K1WLQxNFudKHWn9w4X5CwMb" +
            "PwDx6MG73BmVQqcSfj/gCA5EQ30mKWVDm+7+A1BLAwQUAAAACACHTuJAc2IHVNYJAAB3TwAADQAA" +
            "AHhsL3N0eWxlcy54bWzVXNuP21Qaf0fif7BcwQPaNrHj3GAypZOtpX3Y1UoUCQlQ5Zl4ZiI59pA4" +
            "aIan7pbu7LIqEipQbhI3lfJAh6ugAlr+mSZNnvZf2O+cY/t8Jzm+TJs4zkSaiR1/99/3feeW2Th/" +
            "2HOU1+3+oOu5LVU7V1YV293xOl13r6W+eMk821CVgW+5HcvxXLulHtkD9fzmk09sDPwjx35h37Z9" +
            "BVi4g5a67/sHz5ZKg519u2cNznkHtguf7Hr9nuXDZX+vNDjo21ZnQIh6Tkkvl2ulntV11c0Nd9gz" +
            "e/5A2fGGrt9SjeiWwj75SwduGqrCuLW9DqhyWXn6taHnP/e/P26xN88oZ/505kz5XLl8WXlO8ukr" +
            "Z5M/Z1zOsj/nz1Mmz19W1FKoH1ZGz6ZMoibyDwU1kpXQZpUIXECpnlECg4OrU/CtxPANXRux5jcE" +
            "7qLzSkF0Nzd2PZcHWdchyuTO5sbgDeV1ywH8acTZO57j9RUfYARRpndcq2ezJ0Yn/31w7wZ9at/q" +
            "DwB9jLBikHsUe8GTva7r9cnNEpNxakn3Tyb3vp1+fG362ztx8gTW20SB0JAGJUky5LHYIz/197Zb" +
            "qmlW6ItITXVWmTyV7qoutmdeYN0kr0UK3E4WaF6o/7lMdV+UhYLACjFFgB69kypLywo9QRryZwD0" +
            "BUuLkIgkBVAxzTL8EHNTjcsIlQRhBJf12iKFDZNhQmwzF4pLmXWnqk6P78VmG6xaqBcTjMoLGotP" +
            "6BRoNMCJjbysa7Zr1cVmWVwBidLahJ9FZpogsEo451Yf5/tnfvURMk3LJ3KLrSJ0NDKAMVXXcaKh" +
            "dKVCRllwZ3PjwPJ9u++acKEE7y8dHcAYy4WhPYluiT2X8vRe3zrSdAqHbAQDz+l2iBZ7bTogCvGq" +
            "m/AicreDD7puxz60Yahfo6O5ElI4q3JxsiAz2u28ZLXbzeaSZQXIaaqK3yUzpvK5ahN+Ko1mTW82" +
            "tDINzxL9Gsivr1h+dVXyQwyb7Xr7Yj6xhpn4SmMN0++Vyq+tWD6yvw6p1tBqjUajaVQ0WqyWn2so" +
            "/iuRj/y/EvnI/gr4v16tNqpaUze0nGotiv9K5CP/r0Q+6jX5yo9q7cWt5rJ7+Hxfy9fWQD7qa/nK" +
            "D3x9oUpe+fQ1NIZYSV1Bvs5XfuDrdu2imdcYAuVwvrZG49XlDvej9M1FTE4JQufCyx9f0OWl5Ys5" +
            "9WiJTjJhWrvt9TuwUaUEe0RaGaaU7N7mhmPv+hDyfndvn/z1vQMCAM/3vR686XStPc+1HHhbCinC" +
            "v4QSdrhgM6ul+vt0M4otclhD3wu2H0rkoYB76rNUB6pC6qOgZqhl6rPMmEe0Jag0fI8g1aJ5ihS7" +
            "5gnSrJunWISNWzp5kc6V1UZEkc1GRJDRRkSxCBv51ktWGxFFNhsRQUYbEUVWGyFIVBv4C0LgNyMM" +
            "c6Fnd7rDHmQ5S8igtrOloSWLiGa6BpnpGXWjXDeqeo3hakGyO95w27Ej84J0ME1YpaZ7NlCqSP3K" +
            "nvJyhslJn0ozD5dUEglgUmmy2ipCJkSKULgFnKTrIjyepEbQOqAT7diO8wJpGS/tRu3IaEIkD3fR" +
            "EQE4SkH2nMnRBfIWVl2Dt6z1sIvNDcvp7rk924WdbLvvd3fIPvgOXNps8/pwd4atQU8epPFVrIMD" +
            "58gE+VQ6uwIV+NUWbaf8+kKoB7/1977n2zs+PRpCuu1pVUUegJ39yAWwYKJgHzBd/zbsbdt9kx4X" +
            "4SrkqyUMj8M4CTrCBfWgXMcleI6ddVmLIBv0EMpaqBqHR7CgmHjUSK4HhQOWvwqkpEGPCK1d1InW" +
            "gT81WHcokEMxNuPbRVIZMvMu76Rapja4wjUiWO4JIQB+zoqA3J2L8IDgAPjlGsNFQlcSe/8SehTS" +
            "UEN5pRe2mkLCr1nkoeavmcawGbNuKsPgbt2cHIcLqG1JFUFoEMutCLBZJXVqcTTENUsDdXldhcKW" +
            "4MWtXOdNcTULFE7Q0cwx0tiPOriumH7EDQrrCAPsBD8uN0fQSATU4H7LfbaJOzkCHBzQR1pBzVmZ" +
            "p/DqgV7YXBUyAfogj2iRKgpZfZGWZoh8QoDzLClkCClVEZxaFBVJzgQzSTgLWdRYozZMRmYFRSQK" +
            "N8mhgmqJWkhFqIyFym7sy/XQEtxX0IgjX+pQNYuvJZTwgiqJkwfcWnwt9WKtt5J2E6y1CYOh4pZL" +
            "lDxrMWKrrEUdKm4TRxmuFXdAhLTUi1stUe6shSu1lRfLEt6TZzv0aHO++kh788rh7qNu0uNyzScK" +
            "sxUm5M8mNWzBXnu8fXYY8oXTkhykIZwK0uC+3DZm6VawhAaP0ekcWmYRzd/3+t03oO2RMxHkDJ6a" +
            "8YxEBvdnVjHTsQcKPwAcOhsingyJ4KmQ71y31NHdu5PbbyI3bQ+7Dny9hgEOTrnMEkx+uj26+4+X" +
            "y6+GNGAAoqmTE2+zNHr5KeWsMvr918l3V8fvH4/u3Jx+8cnkPz8oUAVZfMgMgQuusG9dhqdbAlUn" +
            "92+Mrt0KKciAg1PAgp9ELtM1pCAjZ05Bj2/NKjq6fvXBveujf/9r+tG7yERS+zglPZ86S2mkmkgG" +
            "H5xJhX5ZZJbL6JeTUFvSFPjj0CMk9mF1QzpSATkd/bLqrJRaqq6ku3Mmhty5P1+b3rg/fjuKCByE" +
            "xVTSGD788D54d/ztu6G6ZBCJRNGTzLP6jn75YXLyx/TmycOP3pzMSRWhI3Xr+Mevp8dvRSJF6IDa" +
            "EtfG+gjEMcjS00PcSRUpLMafHU+/+EAB6AVEIpTYV4Zm7Z3c+Wr0zluQKONPvokIRfiA2yRKM2kR" +
            "iQghGAxJSCa3vwTXjK/cnhEnwqgqxVFgXFTB6MoO9whsZkgEBkSRG+lSPSKSAj02GpFsutvL2cDC" +
            "SLxsXnVE7MDoQUIUKzsKqi4CCs5MSdiQ6nX8axgbOo3n6oL2MpKTzx+e3IxIROjAoriEZPzllfGn" +
            "t0bX3xtduzr+7PeIVkQPKCyhja3SQBxUaRFRVWnmSMo8D7YIK/hOuEQPVlXmbRCLEnhQQjv+/nh8" +
            "5bdQXzqVQl6WZsDo1r3oebEYwQq/RMT0yj8f3L0TkYgQgtm6hCTWsxBS5lnwBC6DhlSyxLMR/uGf" +
            "v2AGurQOxurB2YggAwdKzIntdZyNiLeKFN6x2kRwqYh4Y//gZrZaxmrD2cygTg6DhNEJNGvsXFBL" +
            "4hVJdKIKAR0UM4CjihIGsf7gbER8wglcCZtYf3A2ImbhCJ2EjcScCK3k0B1q2obUobF6cDYiZg2p" +
            "W2PLL2cjYtaQpqDEnKisQbYJ5khzJ9YczkZEa1Ua5FhzOBsRrVUaZD6thKG9b8F5fXruOxrbA746" +
            "9q41dPxL0Yctlb//K/0KA2PF/2Pe5v8BUEsDBBQAAAAIAIdO4kCLRr+kggEAAMcDAAAUAAAAeGwv" +
            "c2hhcmVkU3RyaW5ncy54bWx9k71OwzAQx3ck3iHyTuMmDW1QEpCQEAMLEogRHcnRWLLPUexAy4TE" +
            "wsDAwgITLwBPwOOA4C1wy+agjPl//HQ+O9nuQsngClsjNOVsPOIsQCp1JWies9OTg60ZC4wFqkBq" +
            "wpwt0bDdYnMjM8YGrksmZ7W1zU4YmrJGBWakGyTnXOpWgXWf7Tw0TYtQmRrRKhlGnG+HCgSxoNQd" +
            "2ZzFY1ZkRhSZLb7uH3/uPj7vX75fH35un7/f3j8/nrLQFlm4CvyFrKh8iUChryl9ISQGTe1G973j" +
            "Y185w/0arK+6OeWFXvgyEC19bQl0CTRPZvHUt1bxPdsCGbfYUUm+bzqlsPXVQ5RSn3SV7pJ0kvju" +
            "X2eIChJ7gx8epZN0FiXTSewDV/EhnMTeFt1zqIEU6AqvUSxE72CuM4QEKbHXuV6I6hwiLsYmLW9i" +
            "bqOoP6rrDYGtUH6nMvE05bOEb3PfcukhmOn+ue21OKlRNj5t7QzxJFDZW+VadE8o4uPezay9/4mh" +
            "+xGLX1BLAwQUAAAACACHTuJAaFmlvzQBAADvAQAADwAAAHhsL3dvcmtib29rLnhtbI1Ry07DMBC8" +
            "I/EP1t6p0/ShEjWpxEv0gioB5WzsTWPVsSPbIeXv2SQq5chpd3a9o5nxenOqDftCH7SzOUwnCTC0" +
            "0iltDzm8vz3drICFKKwSxlnM4RsDbIrrq3Xn/PHTuSMjAhtyqGJsMs6DrLAWYeIatLQpna9FJOgP" +
            "PDQehQoVYqwNT5NkyWuhLYwMmf8PhytLLfHBybZGG0cSj0ZEkh8q3QQo1qU2uB8dMdE0L6Im3ScD" +
            "zIgQH5WOqHKYEXQdXgYLYL5t7lptaHs7S1Lgxa/JnSfQu91r7MJl3kPWaatc96FVrCjB+SqlDMfZ" +
            "M+pDFXNYpctFT8f/UAw5ENVQmR1EvvbZTCnwvm5JB/U+09T4rZoODOczKYzcedaX4eF8vkjJgnRW" +
            "tt5TNve0ySEZjs5/VfwAUEsDBAoAAAAAAIdO4kAAAAAAAAAAAAAAAAAGAAAAX3JlbHMvUEsDBBQA" +
            "AAAIAIdO4kB7OHa8/wAAAN8CAAALAAAAX3JlbHMvLnJlbHOtks9KxDAQxu+C7xDmvk13FRHZdC8i" +
            "7E1kfYCYTP/QJhOSWe2+vUFRLNS6B4+Z+eab33xkuxvdIF4xpo68gnVRgkBvyHa+UfB8eFjdgkis" +
            "vdUDeVRwwgS76vJi+4SD5jyU2i4kkV18UtAyhzspk2nR6VRQQJ87NUWnOT9jI4M2vW5QbsryRsaf" +
            "HlBNPMXeKoh7uwZxOIW8+W9vquvO4D2Zo0PPMyvkVJGddWyQFYyDfKPYvxD1RQYGOc9ydT7L73dK" +
            "h6ytZi0NRVyFmFOK3OVcv3EsmcdcTh+KJaDN+UDT0+fCwZHRW7TLSDqEJaLr/yQyx8Tklnk+NV9I" +
            "cvItq3dQSwMECgAAAAAAh07iQAAAAAAAAAAAAAAAAAkAAAB4bC9fcmVscy9QSwMEFAAAAAgAh07i" +
            "QOXwohjtAAAAugIAABoAAAB4bC9fcmVscy93b3JrYm9vay54bWwucmVsc62Sz2rDMAzG74O9g9F9" +
            "cdKNMUadXsag1617AGMrf2hiB0tbm7efyKFZoHSXXAyfhL/vJ8vb3bnv1A8mamMwUGQ5KAwu+jbU" +
            "Br4O7w8voIht8LaLAQ2MSLAr7++2H9hZlkvUtAMpcQlkoGEeXrUm12BvKYsDBulUMfWWRaZaD9Yd" +
            "bY16k+fPOv31gHLhqfbeQNr7J1CHcZDk/71jVbUO36L77jHwlQhNjU3oPznJeCTGNtXIBhblTIhB" +
            "X4d5XBWGx05ec6aY9K34zZrxLDvCOX2SejqLWwzFmgynmI7UIPLMcSmRbEs6Fxi9+HHlL1BLAwQU" +
            "AAAACACHTuJAqPFac2cBAAANBQAAEwAAAFtDb250ZW50X1R5cGVzXS54bWytlMtOAjEUhvcmvsOk" +
            "WzNTcGGMYWDhZakk4gPU9sA09JaegvD2nilgAkGBjJtJOu35v//8vQxGK2uKJUTU3tWsX/VYAU56" +
            "pd2sZh+Tl/KeFZiEU8J4BzVbA7LR8PpqMFkHwIKqHdasSSk8cI6yASuw8gEczUx9tCLRMM54EHIu" +
            "ZsBve707Lr1L4FKZWg02HDzBVCxMKp5X9HvjJIJBVjxuFrasmokQjJYikVO+dOqAUm4JFVXmNdjo" +
            "gDdkg/GjhHbmd8C27o2iiVpBMRYxvQpLNrjychx9QE6Gqr9Vjtj006mWQBoLSxFU0LasQJWBJCEm" +
            "DT+e/2RLH+Fy+C6jtvpi4gKTt5czDxqWWeZM+MpwbEQE9Z4inUjsTMcQQShsAJI11Z727qgci731" +
            "kdYG/t1AFj1BTnSpgOdvv3MAWeYE8MvH+af3886ww7Qp9coK7c7g5y1C2n2q6d71vpG2vyy888Hz" +
            "Yzb8BlBLAQIUABQAAAAIAIdO4kCo8VpzZwEAAA0FAAATAAAAAAAAAAEAIAAAAO4eAABbQ29udGVu" +
            "dF9UeXBlc10ueG1sUEsBAhQACgAAAAAAh07iQAAAAAAAAAAAAAAAAAYAAAAAAAAAAAAQAAAAVhwA" +
            "AF9yZWxzL1BLAQIUABQAAAAIAIdO4kB7OHa8/wAAAN8CAAALAAAAAAAAAAEAIAAAAHocAABfcmVs" +
            "cy8ucmVsc1BLAQIUAAoAAAAAAIdO4kAAAAAAAAAAAAAAAAAJAAAAAAAAAAAAEAAAAAAAAABkb2NQ" +
            "cm9wcy9QSwECFAAUAAAACACHTuJAuzfZrzABAAA0AgAAEAAAAAAAAAABACAAAAAnAAAAZG9jUHJv" +
            "cHMvYXBwLnhtbFBLAQIUABQAAAAIAIdO4kCpDSVhMQEAADcCAAARAAAAAAAAAAEAIAAAAIUBAABk" +
            "b2NQcm9wcy9jb3JlLnhtbFBLAQIUABQAAAAIAIdO4kCKgsmS/gAAAH8BAAATAAAAAAAAAAEAIAAA" +
            "AOUCAABkb2NQcm9wcy9jdXN0b20ueG1sUEsBAhQACgAAAAAAh07iQAAAAAAAAAAAAAAAAAMAAAAA" +
            "AAAAAAAQAAAAFAQAAHhsL1BLAQIUAAoAAAAAAIdO4kAAAAAAAAAAAAAAAAAJAAAAAAAAAAAAEAAA" +
            "AKIdAAB4bC9fcmVscy9QSwECFAAUAAAACACHTuJA5fCiGO0AAAC6AgAAGgAAAAAAAAABACAAAADJ" +
            "HQAAeGwvX3JlbHMvd29ya2Jvb2sueG1sLnJlbHNQSwECFAAUAAAACACHTuJAi0a/pIIBAADHAwAA" +
            "FAAAAAAAAAABACAAAABBGQAAeGwvc2hhcmVkU3RyaW5ncy54bWxQSwECFAAUAAAACACHTuJAc2IH" +
            "VNYJAAB3TwAADQAAAAAAAAABACAAAABADwAAeGwvc3R5bGVzLnhtbFBLAQIUAAoAAAAAAIdO4kAA" +
            "AAAAAAAAAAAAAAAJAAAAAAAAAAAAEAAAAAEJAAB4bC90aGVtZS9QSwECFAAUAAAACACHTuJA6ibP" +
            "n+cFAAA2GQAAEwAAAAAAAAABACAAAAAoCQAAeGwvdGhlbWUvdGhlbWUxLnhtbFBLAQIUABQAAAAI" +
            "AIdO4kBoWaW/NAEAAO8BAAAPAAAAAAAAAAEAIAAAAPUaAAB4bC93b3JrYm9vay54bWxQSwECFAAK" +
            "AAAAAACHTuJAAAAAAAAAAAAAAAAADgAAAAAAAAAAABAAAAA1BAAAeGwvd29ya3NoZWV0cy9QSwEC" +
            "FAAUAAAACACHTuJAZS/RJWoEAABZDgAAGAAAAAAAAAABACAAAABhBAAAeGwvd29ya3NoZWV0cy9z" +
            "aGVldDEueG1sUEsFBgAAAAARABEABwQAAIYgAAAAAA==";

    @Test
    public void testProjectUpload() throws Exception {
        File file=new File("null/project/tem/车人网（Alex）22_1.xls");
        String s=fileToBase64(file);
//        String base64File = EXCEL;
        String base64File = s;


        String requestURI = "/importData/uploadFile.json";
        byte[] fileBytes = BASE64Util.decode(base64File);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "公众号.xlsx", null, fileBytes);
        MvcResult mvcResult = this.mockMvc.perform(
                fileUpload(requestURI).file(mockMultipartFile).file(mockMultipartFile).param("testInLogin", "no").param("id", "1").content(fileBytes)).andDo(print())
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testimport() throws Exception {
        String str = "[{\"key\":\"\",\"value\":\"Site\"},{\"key\":\"url\",\"value\":\"url\"},{\"key\":\"title\",\"value\":\"title\"},{\"key\":\"summary\",\"value\":\"summary\"},{\"key\":\"content\",\"value\":\"content\"},{\"key\":\"publishtime\",\"value\":\"publishTime\"},{\"key\":\"\",\"value\":\"uid\"},{\"key\":\"author\",\"value\":\"author\"},{\"key\":\"source\",\"value\":\"source\"},{\"key\":\"\",\"value\":\"vtimes\"},{\"key\":\"\",\"value\":\"rtimes\"},{\"key\":\"\",\"value\":\"ftimes\"},{\"key\":\"\",\"value\":\"ltimes\"},{\"key\":\"\",\"value\":\"ctimes\"},{\"key\":\"\",\"value\":\"tag\"},{\"key\":\"\",\"value\":\"集团\"},{\"key\":\"\",\"value\":\"话题\"},{\"key\":\"\",\"value\":\"调性\"}]";
        String requestURI = "/importData/import.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no")
                        .param("typeNo", "dataImport").param("origainField", str)
                        .param("name", "VGC-话题数据-10w.xlsx")
                        .param("typeName", "搜狐内容").param("typeId", "20").param("url", "VGC-话题数据-10w.xlsx")
                        .param("projectId", "375").param("storageTypeTable","news")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void testGetDataSourceType() throws Exception {
        String requestURI = "/importData/getDataSourceType.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    public void testImportList() throws Exception {
        String requestURI = "/importData/importList.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("projectId", "26").param("typeNo", "dataImport")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testDeleteImport() throws Exception {
        String requestURI = "/importData/deleteImport.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("paramId", "1")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testGetDataImportListFromShowData() throws Exception {
        String requestURI = "/importData/getDataImportListFromShowData.json";
        MvcResult mvcResult = this.mockMvc.perform(
                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("detailId","3107").param("projectId","843").param("type","news").param("lastIndexId","14592621")).andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);
    }

    @Test
    @Rollback(value = false)
    public void testGetUploadFile() throws Exception {
//        String requestURI = "/importData/getUploadFile.json";
//        MvcResult mvcResult = this.mockMvc.perform(
//                MockMvcRequestBuilders.post(requestURI).param("testInLogin", "no").param("url","/null/project/tem/1494212971684公众号.xlsx")).andDo(print()).andExpect(status().isOk()).andReturn();
//        String response = mvcResult.getResponse().getContentAsString();
//        assertTrue(com.jeeframework.util.json.JSONUtils.isJSONValid(response));
//        assertTrue(JSONObject.fromObject(response).getInt("code") == 0);

        String fileBase = System.getProperty("upload.dir");
        String fileParamter = "/project/tem/";
        String fileName="1494212416224公众号.xlsx";

        Map<String, String> getTermListByTermNamePostData = new HashMap<String, String>();
        getTermListByTermNamePostData.put("url", fileBase+fileParamter+fileName);
        callRemoteService(HTTP_PROTOCOL + APBS_URL + GET_TERM_LIST_URI, "post", getTermListByTermNamePostData);
    }

    public Object callRemoteService(String serviceURL, String method, Map<String, String> postData) {
        HttpClientHelper httpClientHelper = new HttpClientHelper();

        try {
            HttpResponse getTermListResponse = null;

            if (method.equalsIgnoreCase("get")) {
                getTermListResponse = httpClientHelper.doGet(serviceURL,
                        "utf-8", "utf-8", null,
                        null);
            } else {
                getTermListResponse = httpClientHelper.doPostAndRetBytes(serviceURL, postData, "utf-8", "utf-8", null, null);
            }
            byte[] bytes = getTermListResponse.getContentBytes();
            System.out.println(bytes.length);

            String fileBase = System.getProperty("upload.dir");
            String fileParamter = "/project/tem/";
            String fileDir = fileBase + fileParamter + "14000.xlsx";


            byte2File(bytes,fileBase+fileBase,fileDir);
        } catch (HttpException e) {
            LoggerUtil.errorTrace("segmentJob", e);
        } catch (IOException e) {
            LoggerUtil.errorTrace("segmentJob", e);
        }

        return null;
    }

    public static void byte2File(byte[] buf, String filePath, String fileDir) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(fileDir);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }

    public static void main(String[] args) {

    }
}
