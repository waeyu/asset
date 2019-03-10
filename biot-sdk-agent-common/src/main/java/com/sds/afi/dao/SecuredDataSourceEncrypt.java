package com.sds.afi.dao;

/**
 * 내부용 단순 암복호화를 해주는 유틸
 * 
 * @author 이환기 <hg0503.yi@samsung.com>
 * @since 2010. 6. 16.
 *
 */
public final class SecuredDataSourceEncrypt {
	
	private static final int MASK_SIZE = 256;
	
	private static final String MASK[] = {
		"b7","Qv","hO","Qf","mj","9Q","Ls","oT",
		"Dx","tp","AC","fj","6J","IP","Ol","YR",
		"Jy","rw","vj","P2","1d","Hn","wL","XR",
		"S3","LQ","2V","rb","hx","Dh","Jg","IH",
		"a8","am","Vp","MY","qc","Lk","bR","sp",		
		"S7","6O","Ck","wZ","CS","lE","mz","rF",
		"9R","eN","lg","vD","Fc","lx","QK","ZL",
		"On","GQ","yT","aJ","Tx","V0","ho","4N",		
		"NA","7s","Ki","ZH","sX","uW","Np","0M",
		"UY","I7","Bk","kb","VG","c2","FT","Fw",		
		"Lx","0i","nO","37","c5","L2","Lj","1e",
		"NH","KN","l7","4o","yc","hp","7z","Ga",
		"b0","dE","XO","nx","wE","7j","KJ","wA",
		"Ra","uu","0Q","gI","bb","CV","IK","y5",
		"5X","LF","BH","WH","hi","kQ","OQ","id",		
		"OW","Pk","ty","eD","DE","GL","Ht","OR",		
		"N7","rS","uw","qM","7Y","c0","1J","Om",
		"Sn","8x","aw","16","lj","8E","Lz","u4",
		"Hc","Ac","5S","hz","Y1","7H","TJ","5M",
		"Wr","OD","ce","eP","Ws","pp","mL","UE",
		"nQ","YM","iR","nn","LO","ee","2Y","mZ",
		"HZ","LD","Ih","5h","Xc","9T","n1","Xu",
		"T4","3C","LG","48","rR","tL","xa","Vh",
		"H6","tx","rf","om","0q","NC","un","1U",		
		"XY","Gf","JD","Xq","tu","aF","oo","mx",
		"vc","aW","gp","kP","oE","Gt","Cn","Fy",
		"tJ","NT","7G","Gi","ro","Co","cf","Cm",
		"dv","Ab","WD","3W","Gy","jc","Xp","L6",
		"qn","Hr","lk","UC","pY","VV","jY","65",
		"j5","F5","SH","Kw","n9","XQ","5O","ni",
		"rH","AS","us","v3","RE","NB","lQ","xn",
		"hg","LE","qm","ri","bz","9B","g6","e5"};

	/* 암호화 */
	public static String encrypt(String sData) {
		int i = 0;
		int len;
		String seedChar = "tmfrlWkd";
		String src;
		StringBuilder encret = new StringBuilder();

		src = sData;
		len = src.length();

		for (i = 0; i < len; i++) {
			encret.append(MASK[src.charAt(i) ^ seedChar.charAt(i % 8)].charAt(0));
			encret.append(MASK[src.charAt(i) ^ seedChar.charAt(i % 8)].charAt(1));
		}

		return encret.toString();
	}

	/* 복호화 */
	public static String decrypt(String sData) {
		int i;
		int j;
		int len;
		StringBuilder ch = new StringBuilder();
		StringBuilder decret = new StringBuilder();
		String seedChar = "tmfrlWkd";

		String eData;

		eData = sData;
		len = eData.length() / 2;

		for (i = 0; i < len; i++) {
			ch.setLength(0);
			ch.append(eData.charAt(i * 2));
			ch.append(eData.charAt(i * 2 + 1));

			for (j = 0; j < MASK_SIZE; j++) {
				if (MASK[j].equals(ch.toString())) {
					decret.append((char) (j ^ seedChar.charAt(i % 8)));
					break;
				}
			}
			if (j == MASK_SIZE) {
				throw new IllegalArgumentException("Decrypt Fail!");
			}
		}

		return decret.toString();
	}
	
	public static void main(String[] args) {
		if(args.length != 1 ) {
			System.out.println("argument is 1");
		}
		else {
			System.out.println("["+encrypt(args[0])+"]");
		}
	}

}
