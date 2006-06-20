package ncsa.d2k.modules.projects.dtcheng.clustering;

/*
 
p566
3.402E3 5.492E3 3.058E3 5.248E3 1(364) 2(384) 3(350) 4(358) 5(400) 6(343) 7(372) 8(315) 9(345) 10(399) 11(368) 12(417) 13(369) 14(368) 15(315) 16(403) 

p319


1sec ahead forecast

using 4 clusters:
00001 3.2724E3 4.9746E3 3.3815E3 4.5586E3 1(1314) 2(1302) 3(1325) 4(1267) 
09978 3.0185E3 4.9746E3 3.0285E3 4.5586E3 1(1439) 2(1132) 3(1425) 4(1212) 

using 16 clusters:
00002 3.2342E3 4.9746E3 3.3433E3 4.5586E3 1(347) 2(332) 3(326) 4(308) 5(300) 6(341) 7(359) 8(303) 9(308) 10(338) 11(345) 12(333) 13(315) 14(311) 15(316) 16(326) 
09920 2.9905E3 4.9746E3 3.0149E3 4.5586E3 1(349) 2(352) 3(320) 4(308) 5(295) 6(345) 7(387) 8(272) 9(299) 10(327) 11(343) 12(337) 13(319) 14(286) 15(323) 16(346) 

using 64 clusters:
00001 3.1508E3 4.9746E3 3.2206E3 4.5586E3 1(80) 2(95) 3(97) 4(75) 5(96) 6(77) 7(80) 8(80) 9(74) 10(83) 11(80) 12(89) 13(76) 14(77) 15(78) 16(77) 17(70) 18(79) 19(73) 20(78) 21(73) 22(90) 23(104) 24(74) 25(101) 26(91) 27(88) 28(79) 29(55) 30(76) 31(86) 32(85) 33(71) 34(89) 35(78) 36(70) 37(73) 38(84) 39(94) 40(87) 41(84) 42(94) 43(93) 44(75) 45(79) 46(86) 47(96) 48(72) 49(72) 50(85) 51(71) 52(87) 53(78) 54(83) 55(75) 56(75) 57(84) 58(83) 59(69) 60(80) 61(65) 62(85) 63(90) 64(85) 
09959 2.9239E3 4.9746E3 2.9151E3 4.5586E3 1(65) 2(84) 3(107) 4(76) 5(91) 6(80) 7(72) 8(82) 9(70) 10(80) 11(78) 12(92) 13(75) 14(77) 15(82) 16(76) 17(65) 18(84) 19(71) 20(60) 21(67) 22(98) 23(97) 24(77) 25(110) 26(83) 27(91) 28(68) 29(56) 30(73) 31(89) 32(87) 33(70) 34(85) 35(88) 36(82) 37(89) 38(100) 39(95) 40(94) 41(92) 42(86) 43(95) 44(73) 45(83) 46(91) 47(86) 48(70) 49(67) 50(84) 51(64) 52(95) 53(75) 54(58) 55(75) 56(85) 57(84) 58(97) 59(71) 60(84) 61(59) 62(85) 63(98) 64(85) 

using 256 clusters:
00000 3.0449E3 4.9746E3 3.0510E3 4.5586E3 1(20) 2(11) 3(21) 4(28) 5(25) 6(25) 7(19) 8(26) 9(19) 10(30) 11(26) 12(22) 13(17) 14(14) 15(22) 16(22) 17(24) 18(25) 19(31) 20(16) 21(20) 22(20) 23(13) 24(24) 25(21) 26(16) 27(21) 28(22) 29(19) 30(23) 31(14) 32(24) 33(20) 34(20) 35(15) 36(19) 37(21) 38(20) 39(18) 40(24) 41(21) 42(24) 43(21) 44(14) 45(23) 46(20) 47(24) 48(22) 49(16) 50(14) 51(23) 52(23) 53(19) 54(21) 55(19) 56(18) 57(25) 58(10) 59(26) 60(17) 61(17) 62(23) 63(12) 64(25) 65(16) 66(19) 67(14) 68(20) 69(22) 70(21) 71(16) 72(20) 73(20) 74(17) 75(19) 76(17) 77(18) 78(19) 79(19) 80(22) 81(21) 82(14) 83(22) 84(16) 85(26) 86(18) 87(26) 88(20) 89(34) 90(25) 91(23) 92(22) 93(25) 94(16) 95(17) 96(16) 97(23) 98(17) 99(37) 100(24) 101(28) 102(22) 103(17) 104(24) 105(25) 106(18) 107(21) 108(24) 109(17) 110(23) 111(20) 112(19) 113(15) 114(17) 115(12) 116(11) 117(22) 118(14) 119(18) 120(22) 121(22) 122(29) 123(17) 124(18) 125(22) 126(20) 127(21) 128(22) 129(17) 130(18) 131(22) 132(14) 133(24) 134(15) 135(27) 136(23) 137(21) 138(17) 139(17) 140(23) 141(17) 142(19) 143(13) 144(21) 145(12) 146(24) 147(21) 148(16) 149(16) 150(26) 151(24) 152(18) 153(31) 154(30) 155(17) 156(16) 157(26) 158(24) 159(16) 160(21) 161(18) 162(28) 163(24) 164(14) 165(18) 166(33) 167(21) 168(21) 169(31) 170(19) 171(22) 172(21) 173(19) 174(15) 175(27) 176(14) 177(16) 178(29) 179(17) 180(17) 181(24) 182(19) 183(27) 184(16) 185(23) 186(29) 187(23) 188(21) 189(13) 190(17) 191(28) 192(14) 193(23) 194(15) 195(18) 196(16) 197(20) 198(18) 199(20) 200(28) 201(15) 202(17) 203(22) 204(17) 205(17) 206(22) 207(18) 208(30) 209(31) 210(14) 211(18) 212(15) 213(23) 214(21) 215(15) 216(24) 217(20) 218(14) 219(23) 220(18) 221(21) 222(18) 223(16) 224(20) 225(23) 226(15) 227(24) 228(22) 229(20) 230(18) 231(23) 232(22) 233(16) 234(13) 235(18) 236(22) 237(24) 238(24) 239(23) 240(9) 241(18) 242(16) 243(14) 244(17) 245(19) 246(24) 247(25) 248(17) 249(24) 250(16) 251(27) 252(23) 253(28) 254(20) 255(18) 256(20) 



10sec ahead forecast
4 clusters:
00000 3.2779E3 5.6299E3 3.3882E3 5.1228E3 1(1314) 2(1297) 3(1324) 4(1268) 
09417 3.1398E3 5.6299E3 3.1747E3 5.1228E3 1(1359) 2(1248) 3(1333) 4(1263) 
16 clusters:
00000 3.2552E3 5.6299E3 3.3632E3 5.1228E3 1(347) 2(333) 3(326) 4(308) 5(300) 6(340) 7(357) 8(300) 9(308) 10(338) 11(345) 12(333) 13(315) 14(311) 15(316) 16(326) 
09864 3.1055E3 5.6299E3 3.1379E3 5.1228E3 1(333) 2(342) 3(330) 4(309) 5(308) 6(355) 7(375) 8(262) 9(303) 10(321) 11(344) 12(324) 13(327) 14(312) 15(320) 16(338) 
64 clusters:
00001 3.2060E3 5.6299E3 3.2689E3 5.1228E3 1(80) 2(95) 3(97) 4(75) 5(96) 6(77) 7(80) 8(80) 9(74) 10(83) 11(80) 12(89) 13(76) 14(77) 15(78) 16(77) 17(70) 18(79) 19(73) 20(78) 21(73) 22(89) 23(104) 24(74) 25(100) 26(89) 27(88) 28(79) 29(54) 30(76) 31(86) 32(85) 33(71) 34(89) 35(78) 36(70) 37(73) 38(84) 39(94) 40(87) 41(84) 42(93) 43(93) 44(75) 45(79) 46(85) 47(97) 48(72) 49(72) 50(85) 51(71) 52(87) 53(78) 54(83) 55(75) 56(75) 57(84) 58(83) 59(69) 60(80) 61(65) 62(85) 63(90) 64(86) 
09958 3.0916E3 5.6299E3 3.1614E3 5.1228E3 1(74) 2(90) 3(91) 4(77) 5(79) 6(84) 7(81) 8(86) 9(73) 10(82) 11(77) 12(94) 13(80) 14(72) 15(88) 16(84) 17(67) 18(81) 19(67) 20(75) 21(74) 22(93) 23(110) 24(81) 25(110) 26(87) 27(88) 28(81) 29(49) 30(77) 31(77) 32(86) 33(70) 34(84) 35(71) 36(72) 37(74) 38(76) 39(90) 40(81) 41(80) 42(93) 43(90) 44(78) 45(82) 46(89) 47(99) 48(63) 49(77) 50(88) 51(80) 52(93) 53(82) 54(86) 55(81) 56(75) 57(83) 58(79) 59(66) 60(85) 61(63) 62(80) 63(90) 64(88) 
256 clusters:
00002 3.2276E3 5.6299E3 3.1798E3 5.1228E3 1(20) 2(11) 3(21) 4(28) 5(25) 6(25) 7(19) 8(26) 9(19) 10(30) 11(26) 12(22) 13(17) 14(14) 15(22) 16(22) 17(24) 18(25) 19(31) 20(16) 21(20) 22(20) 23(13) 24(24) 25(21) 26(16) 27(21) 28(22) 29(19) 30(23) 31(14) 32(24) 33(20) 34(20) 35(15) 36(19) 37(21) 38(20) 39(18) 40(24) 41(21) 42(24) 43(21) 44(14) 45(23) 46(20) 47(24) 48(22) 49(16) 50(14) 51(23) 52(23) 53(19) 54(21) 55(19) 56(18) 57(25) 58(10) 59(26) 60(17) 61(17) 62(23) 63(12) 64(25) 65(16) 66(20) 67(14) 68(20) 69(22) 70(21) 71(16) 72(20) 73(20) 74(17) 75(19) 76(17) 77(18) 78(19) 79(19) 80(22) 81(21) 82(14) 83(22) 84(16) 85(25) 86(18) 87(26) 88(20) 89(34) 90(25) 91(23) 92(22) 93(25) 94(16) 95(17) 96(16) 97(22) 98(17) 99(37) 100(24) 101(28) 102(22) 103(16) 104(24) 105(25) 106(18) 107(21) 108(24) 109(17) 110(23) 111(20) 112(19) 113(15) 114(17) 115(12) 116(10) 117(22) 118(14) 119(18) 120(22) 121(22) 122(29) 123(17) 124(18) 125(22) 126(20) 127(21) 128(22) 129(17) 130(18) 131(22) 132(14) 133(24) 134(15) 135(27) 136(23) 137(21) 138(17) 139(17) 140(23) 141(17) 142(19) 143(13) 144(21) 145(12) 146(24) 147(21) 148(16) 149(16) 150(26) 151(24) 152(18) 153(31) 154(30) 155(17) 156(16) 157(26) 158(24) 159(16) 160(21) 161(18) 162(28) 163(24) 164(14) 165(18) 166(33) 167(21) 168(21) 169(31) 170(19) 171(22) 172(21) 173(19) 174(15) 175(27) 176(14) 177(17) 178(29) 179(17) 180(17) 181(24) 182(19) 183(25) 184(16) 185(23) 186(29) 187(23) 188(21) 189(13) 190(17) 191(28) 192(14) 193(23) 194(15) 195(18) 196(16) 197(19) 198(18) 199(20) 200(28) 201(15) 202(17) 203(22) 204(17) 205(17) 206(22) 207(18) 208(30) 209(31) 210(14) 211(18) 212(15) 213(23) 214(21) 215(15) 216(24) 217(20) 218(14) 219(23) 220(18) 221(21) 222(18) 223(16) 224(20) 225(23) 226(15) 227(24) 228(22) 229(20) 230(18) 231(23) 232(22) 233(16) 234(13) 235(18) 236(22) 237(24) 238(24) 239(23) 240(9) 241(18) 242(16) 243(14) 244(17) 245(19) 246(24) 247(25) 248(17) 249(24) 250(16) 251(27) 252(23) 253(28) 254(20) 255(18) 256(20) 
07420 3.1235E3 5.6299E3 3.1991E3 5.1228E3 1(16) 2(14) 3(18) 4(22) 5(25) 6(26) 7(17) 8(29) 9(17) 10(28) 11(23) 12(22) 13(20) 14(19) 15(24) 16(20) 17(23) 18(20) 19(29) 20(19) 21(25) 22(20) 23(8) 24(20) 25(18) 26(16) 27(22) 28(26) 29(18) 30(25) 31(14) 32(21) 33(22) 34(22) 35(17) 36(19) 37(18) 38(21) 39(19) 40(21) 41(23) 42(26) 43(22) 44(13) 45(23) 46(20) 47(21) 48(20) 49(14) 50(17) 51(22) 52(21) 53(21) 54(15) 55(16) 56(19) 57(28) 58(15) 59(26) 60(22) 61(22) 62(28) 63(14) 64(19) 65(15) 66(20) 67(15) 68(20) 69(26) 70(24) 71(15) 72(22) 73(21) 74(22) 75(17) 76(17) 77(15) 78(20) 79(18) 80(23) 81(18) 82(16) 83(22) 84(14) 85(22) 86(19) 87(24) 88(24) 89(35) 90(29) 91(23) 92(18) 93(23) 94(17) 95(19) 96(16) 97(27) 98(21) 99(35) 100(24) 101(23) 102(24) 103(23) 104(25) 105(25) 106(18) 107(22) 108(28) 109(19) 110(24) 111(20) 112(23) 113(15) 114(17) 115(12) 116(8) 117(24) 118(12) 119(15) 120(23) 121(24) 122(24) 123(21) 124(22) 125(23) 126(25) 127(21) 128(18) 129(14) 130(20) 131(23) 132(12) 133(27) 134(13) 135(25) 136(25) 137(23) 138(15) 139(19) 140(24) 141(21) 142(20) 143(8) 144(18) 145(13) 146(21) 147(19) 148(18) 149(18) 150(23) 151(24) 152(18) 153(32) 154(33) 155(14) 156(18) 157(26) 158(24) 159(16) 160(23) 161(17) 162(23) 163(21) 164(14) 165(25) 166(33) 167(17) 168(23) 169(28) 170(20) 171(20) 172(17) 173(21) 174(14) 175(25) 176(13) 177(18) 178(28) 179(17) 180(19) 181(26) 182(22) 183(25) 184(14) 185(23) 186(25) 187(23) 188(23) 189(12) 190(22) 191(29) 192(15) 193(26) 194(17) 195(16) 196(16) 197(20) 198(17) 199(17) 200(28) 201(11) 202(15) 203(23) 204(17) 205(22) 206(17) 207(17) 208(24) 209(28) 210(14) 211(21) 212(16) 213(21) 214(19) 215(16) 216(24) 217(19) 218(14) 219(23) 220(16) 221(25) 222(16) 223(12) 224(19) 225(18) 226(16) 227(25) 228(22) 229(19) 230(21) 231(21) 232(18) 233(15) 234(13) 235(17) 236(22) 237(24) 238(21) 239(22) 240(9) 241(18) 242(15) 243(11) 244(15) 245(22) 246(27) 247(27) 248(21) 249(25) 250(20) 251(25) 252(26) 253(25) 254(16) 255(19) 256(19) 



100sec ahead forecast
4 clusters
00001 3.2561E3 5.9432E3 3.3889E3 6.0168E3 1(1302) 2(1286) 3(1319) 4(1251) 
09966 3.1871E3 5.9432E3 3.3109E3 6.0168E3 1(1343) 2(1255) 3(1340) 4(1220) 

1000sec ahead forecast (should be impossible because you will be spanning at least two different ecogs)
4 clusters
00000 2.7126E3 5.7772E3 3.2104E3 5.9491E3 1(1181) 2(1165) 3(1198) 4(1164) 
09272 2.7090E3 5.7772E3 3.2202E3 5.9491E3 1(1174) 2(1177) 3(1205) 4(1152) 

*/


import java.text.DecimalFormat;
import java.util.Random;
import ncsa.d2k.core.modules.ComputeModule;




public class ClusterDoubleArrays extends ComputeModule {

  private long RandomSeed = 123;

  public void setRandomSeed(long value) {
    this.RandomSeed = value;
  }

  public long getRandomSeed() {
    return this.RandomSeed;
  }

  private String FormatPattern = "0.000E0";

  public void setFormatPattern(String value) {
    this.FormatPattern = value;
  }

  public String getFormatPattern() {
    return this.FormatPattern;
  }

  private int PredictionRange = 1;

  public void setPredictionRange(int value) {
    this.PredictionRange = value;
  }

  public int getPredictionRange() {
    return this.PredictionRange;
  }


  private int NumClusters = 2;

  public void setNumClusters(int value) {
    this.NumClusters = value;
  }

  public int getNumClusters() {
    return this.NumClusters;
  }


  private int UpdateGraphInterval = 10;

  public void setUpdateGraphInterval(int value) {
    this.UpdateGraphInterval = value;
  }

  public int getUpdateGraphInterval() {
    return this.UpdateGraphInterval;
  }

  private int NumRounds = 1000;

  public void setNumRounds(int value) {
    this.NumRounds = value;
  }

  public int getNumRounds() {
    return this.NumRounds;
  }

  public String getModuleInfo() {
    return "This modules clusters using random exchange to form a specified number of clusters.  "
        + "The first half of the examples are used for training and the second half is used for testing.  "
        + "The predictive task is to predict the next feature vector given the last one.  ";
  }

  public String getModuleName() {
    return "ClusterDoubleArrays";
  }

  public String[] getInputTypes() {
    String[] types = { "[[D" };
    return types;
  }

  public String getInputName(int i) {
    switch (i) {
    case 0:
      return "2D Double Array";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String getInputInfo(int i) {
    switch (i) {
    case 0:
      return "2D Double Array";
    default:
      return "NO SUCH INPUT!";
    }
  }

  public String[] getOutputTypes() {
    String[] types = { "[[D" };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
    case 0:
      return "Clusters";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
    case 0:
      return "Clusters";
    default:
      return "NO SUCH OUTPUT!";
    }
  }

  double[] CalculateErrors(int StartExampleIndex, int EndExampleIndex) {

    double PredictiveError = 0.0;
    double ConstantPredictionError = 0.0;

    for (int e = StartExampleIndex; e <= EndExampleIndex; e++) {

      // find current cluster

      double MinDistance = Double.MAX_VALUE;
      int BestClusterIndex = -1;

      for (int i = 0; i < NumClusters; i++) {

        // find cluster of current example
        double VarianceSum = 0.0;

        for (int f = 0; f < NumFeatures; f++) {
          double difference = ClusterCentroids[i][f] - data[e][f];
          VarianceSum += difference * difference;
        }

        double Distance = VarianceSum / NumFeatures;

        if (Distance < MinDistance) {
          MinDistance = Distance;
          BestClusterIndex = i;
        }
      }

      // measure error for predicting next example using current cluster
      double VarianceSum = 0.0;

      for (int f = 0; f < NumFeatures; f++) {
        double difference = ClusterCentroids[BestClusterIndex][f] - data[e + PredictionRange][f];
        VarianceSum += difference * difference;
      }

      double Distance = VarianceSum / NumFeatures;

      PredictiveError += Distance;

      // measure error for predicting next example equals current examples
      double BaselineVarianceSum = 0.0;

      for (int f = 0; f < NumFeatures; f++) {
        double difference = data[e][f] - data[e + PredictionRange][f];
        BaselineVarianceSum += difference * difference;
      }

      double ConstantPredictionDistance = BaselineVarianceSum / NumFeatures;

      ConstantPredictionError += ConstantPredictionDistance;
      //System.out.println("BestClusterIndex = " + BestClusterIndex);

    }

    if (false) {
      System.out.println("ClusterBasedPredictionError = " + PredictiveError);
      System.out.println("ConstantPredictionError     = " + ConstantPredictionError);
    }

    double[] errors = { PredictiveError, ConstantPredictionError };

    return errors;
  }
  double[] ResubstitutionErrors = null;

  
  
  
  
  void ReportProgress(int r) {

    DecimalFormat Format1 = new DecimalFormat(FormatPattern);
    DecimalFormat Format2 = new DecimalFormat("00000");
    double[] PredictiveErrors = CalculateErrors(NumTrainExamples, NumExamples - 1);
    System.out.print(Format2.format(r) + " " + Format1.format(ResubstitutionErrors[0]) + " " + Format1.format(ResubstitutionErrors[1]) + " " + Format1.format(PredictiveErrors[0]) + " "
        + Format1.format(PredictiveErrors[1]) + " ");

    for (int c = 0; c < NumClusters; c++) {
      System.out.print((c + 1) + "(" + ClusterNumElements[c] + ") ");
    }
    System.out.println();
  }


  double[][] ClusterCentroids = null;

  int[] ClusterNumElements = null;

  int[][] ClusterElements = null;

  double[][] data = null;

  int NumExamples = -1;

  int NumTrainExamples = -1;

  int NumTestExamples = -1;

  int NumFeatures = -1;

  public void doit() {

    data = (double[][]) this.pullInput(0);

    if (data == null) {
      this.pushOutput(null, 0);
      return;
    }

    NumExamples = data.length - PredictionRange;
    NumFeatures = data[0].length;

    NumTrainExamples = NumExamples / 2;
    NumTestExamples = NumExamples - NumTrainExamples;

    System.out.println("NumExamples      = " + NumExamples);
    System.out.println("NumTrainExamples = " + NumTrainExamples);
    System.out.println("NumTestExamples  = " + NumTestExamples);

    // calculate grand average

    double[] OverallCentroid = new double[NumFeatures];

    for (int i = 0; i < NumTrainExamples; i++) {
      for (int f = 0; f < NumFeatures; f++) {
        OverallCentroid[f] += data[i][f];
      }
    }
    for (int f = 0; f < NumFeatures; f++) {
      OverallCentroid[f] /= NumTrainExamples;
    }
    if (false) {
      for (int f = 0; f < NumFeatures; f++) {
        System.out.println("OverallCentroid[" + f + "] = " + OverallCentroid[f]);
      }
    }

    // cluster

    ClusterCentroids = new double[NumClusters][NumFeatures];
    
    ClusterNumElements = new int[NumClusters];
    ClusterElements = new int[NumClusters][NumTrainExamples];

    Random generator = new Random(RandomSeed);

    for (int e = 0; e < NumTrainExamples; e++) {

      double random = generator.nextDouble();

      //System.out.println("random = " + random);

      int ClusterIndex = (int) (random * NumClusters);

      //System.out.println("ClusterIndex = " + ClusterIndex);

      ClusterElements[ClusterIndex][ClusterNumElements[ClusterIndex]] = e;

      for (int f = 0; f < NumFeatures; f++) {
        ClusterCentroids[ClusterIndex][f] += data[e][f];
      }

      ClusterNumElements[ClusterIndex]++;
    }

    for (int c = 0; c < NumClusters; c++) {
      for (int f = 0; f < NumFeatures; f++) {
        ClusterCentroids[c][f] /= ClusterNumElements[c];
      }
    }

    if (false) {
      for (int i = 0; i < NumClusters; i++) {
        System.out.println("ClusterNumElements[" + i + "] = " + ClusterNumElements[i]);
        for (int f = 0; f < NumFeatures; f++) {

          System.out.println("ClusterCentroids[" + i + "][" + f + "] = " + ClusterCentroids[i][f]);
        }
      }
    }

    // calculate error of clustering through sequential prediction
    double[] CurrentErrors = CalculateErrors(0, NumTrainExamples - 1);
    int NumExchanges = 0;

    if (NumClusters > 1) {

      for (int r = 0; r < NumRounds; r++) {

        // randomly move example

        int FromClusterIndex = (int) (generator.nextInt(NumClusters) );
        int ToClusterIndex = (int) (generator.nextInt(NumClusters - 1));

        if (ToClusterIndex >= FromClusterIndex) {
          ToClusterIndex += 1;
        }

        int FromClusterExampleIndex = (int) (generator.nextDouble() * ClusterNumElements[FromClusterIndex]);
        int ToClusterExampleIndex = (int) ClusterNumElements[ToClusterIndex];

        //System.out.println("FromClusterIndex " + FromClusterIndex);
        //System.out.println("ToClusterIndex " + ToClusterIndex);
        //System.out.println("FromClusterExampleIndex " +
        // FromClusterExampleIndex);
        //System.out.println("ToClusterExampleIndex " + ToClusterExampleIndex);

        int LastExampleIndex1 = ClusterElements[ToClusterIndex][ToClusterExampleIndex];
        int LastExampleIndex2 = ClusterElements[FromClusterIndex][FromClusterExampleIndex];

        ClusterElements[ToClusterIndex][ToClusterExampleIndex] = ClusterElements[FromClusterIndex][FromClusterExampleIndex];
        ClusterElements[FromClusterIndex][FromClusterExampleIndex] = ClusterElements[FromClusterIndex][ClusterNumElements[FromClusterIndex] - 1];

        ClusterNumElements[FromClusterIndex]--;
        ClusterNumElements[ToClusterIndex]++;

        //cacluate new centroid        
        for (int c = 0; c < NumClusters; c++) {
          for (int f = 0; f < NumFeatures; f++) {
            ClusterCentroids[c][f] = 0.0;
          }
        }
        for (int c = 0; c < NumClusters; c++) {
          for (int e = 0; e < ClusterNumElements[c]; e++)
            for (int f = 0; f < NumFeatures; f++) {
              ClusterCentroids[c][f] += data[ClusterElements[c][e]][f];
            }

          for (int f = 0; f < NumFeatures; f++) {
            ClusterCentroids[c][f] /= ClusterNumElements[c];
          }

        }

        //calculate resubstitution error
        ResubstitutionErrors = CalculateErrors(0, NumTrainExamples - 1);

        double DeltaError = CurrentErrors[0] - ResubstitutionErrors[0];

        //System.out.println("DeltaError = " + DeltaError);

        // if error increases undo change
        if (DeltaError < 0) {

          //System.out.println("Reversing Change");

          // undo change
          ClusterNumElements[ToClusterIndex]--;
          ClusterNumElements[FromClusterIndex]++;
          ClusterElements[FromClusterIndex][FromClusterExampleIndex] = LastExampleIndex2;
          ClusterElements[ToClusterIndex][ToClusterExampleIndex] = LastExampleIndex1;

        } else {
          CurrentErrors = ResubstitutionErrors;

          ReportProgress(r);


          NumExchanges++;
        }
        
        
        if (r % UpdateGraphInterval == 0) {

          double[][] ClusterCentroidsCopy = new double[NumClusters][NumFeatures];

          for (int c = 0; c < NumClusters; c++) {
            for (int f = 0; f < NumFeatures; f++) {
              ClusterCentroidsCopy[c][f] = ClusterCentroids[c][f];
            }
          }

          this.pushOutput(ClusterCentroidsCopy, 0);
        }
      }
    }

    System.out.println();
    System.out.println();
    System.out.println("Final Results");
    System.out.println("NumExchanges = " + NumExchanges);
    ReportProgress(NumRounds);

  }
}