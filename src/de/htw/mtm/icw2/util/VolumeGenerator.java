package de.htw.mtm.icw2.util;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class VolumeGenerator {
	public static ByteBuffer generateSphere(int n) {
		ByteBuffer texels = BufferUtils.createByteBuffer(n*n*n*4);
		
		byte b = (byte) (255 & 0xFF);
		byte c = 0;
		
		for (int z = 0; z < n; z++) {
			for (int y = 0; y < n; y++) {
				for (int x = 0; x < n; x++) {
					
					Vector3f v = new Vector3f(x,y,z);
					v = v.add(new Vector3f(n/-2, n/-2, n/-2));
					v = v.divide(n/-2);
					float t = 1.f / (float) n;
					v = v.add(new Vector3f(-t, -t, -t));
					
					byte red = (byte)((int)(255.f / n * y) & 0xFF);
					texels.put(red); texels.put(c); texels.put(c);
					if ((v.x * v.x) + (v.y * v.y) + (v.z * v.z) <= 1) {
						 texels.put(b);
					} else {
						texels.put(c);
					}
				}
			}
		}
		texels.rewind();
		
		return texels;
	}
	
	public static ByteBuffer generateCylinder(int n) {
		ByteBuffer texels = BufferUtils.createByteBuffer(n*n*n*4);
		
		byte b = (byte) (255 & 0xFF);
		byte c = 0;
		
		for (int z = 0; z < n; z++) {
			for (int y = 0; y < n; y++) {
				for (int x = 0; x < n; x++) {
					
					Vector3f v = new Vector3f(x,y,z);
					v = v.add(new Vector3f(n/-2, n/-2, n/-2));
					v = v.divide(n/-2);
					float t = 1.f / (float) n;
					v = v.add(new Vector3f(-t, -t, -t));
					
					int tmp = (int) (255.f / n * x);
					//int tmp = (int) Math.max(Math.min(255.f / n * x, 255),0);
					byte green = (byte)(tmp & 0xFF);
					texels.put(c); texels.put(green); texels.put(c);
					if ((v.x * v.x) + (v.z * v.z) <= 1) {
						 texels.put(b);
					} else {
						texels.put(c);
					}
				}
			}
		}
		texels.rewind();
		
		return texels;
	}
}
