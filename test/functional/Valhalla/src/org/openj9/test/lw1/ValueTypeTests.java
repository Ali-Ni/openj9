package org.openj9.test.lw1;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import org.testng.Assert;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

@Test(groups = { "level.sanity" })
public class ValueTypeTests {

	static Lookup lookup = MethodHandles.lookup();
	static Class point2DClass = null;
	static Method makePoint2D = null;
	static MethodHandle getX = null;
	static MethodHandle getY = null;
	/*
	 * Create a value type
	 * 
	 * value Point2D {
	 * 	int x;
	 * 	int y;
	 * }
	 */
	@Test(priority=1)
	static public void testCreatePoint2D() throws Throwable {
		String fields[] = {"x:I", "y:I"};
		point2DClass = new ValueTypeGenerator().getClass("Point2D", ValueTypeGenerator.generateValue("Point2D", fields));
		System.out.println("getClass success");
		makePoint2D = point2DClass.getDeclaredMethod("makeValue", new Class[] {int.class, int.class});
		System.out.println("getDeclaredMethodSuccess");
		
		getX = generateGetter(point2DClass, "x", int.class);
		MethodHandle withX = generateWither(point2DClass, "x", int.class);
		getY = generateGetter(point2DClass, "y", int.class);
		MethodHandle withY = generateWither(point2DClass, "y", int.class);
		
		Object point2D = makePoint2D.invoke(null, 2, 4);
		
		assertEquals(2, getX.invoke(point2D));
		assertEquals(4, getY.invoke(point2D));
		
		point2D = withX.invoke(point2D, 1);
		point2D = withY.invoke(point2D, 3);
		
		assertEquals(1, getX.invoke(point2D));
		assertEquals(3, getY.invoke(point2D));
	}
	
	/*
	 * Test with nested values
	 * 
	 * class Line2DRef {
	 * 	Point2D st;
	 * 	Point2D en;
	 * }
	 * 
	 */
	
	@Test(priority=2)
	static public void testCreateLine2DRef() throws Throwable {
		String fields[] = {"st:LPoint2D;:value", "en:LPoint2D;:value"};
		Class line2DRefClass = new ValueTypeGenerator().getClass("Line2DRef", ValueTypeGenerator.generateRefObject("Line2DRef", fields));
		Method makeLine2DRef = line2DRefClass.getDeclaredMethod("makeRef", new Class[] {point2DClass, point2DClass});
		MethodHandle getSt = generateGetter(line2DRefClass, "st", point2DClass);
		MethodHandle setSt = generateSetter(line2DRefClass, "st", point2DClass);
		MethodHandle getEn = generateGetter(line2DRefClass, "en", point2DClass);
		MethodHandle setEn = generateSetter(line2DRefClass, "en", point2DClass);
		
		Object st = makePoint2D.invoke(null, 1 ,1);
		Object en = makePoint2D.invoke(null, 4 ,4);
		
		Object line2DRef = makeLine2DRef.invoke(null, st, en);
				
		assertEquals(getX.invoke(st), getX.invoke(getSt.invoke(line2DRef)));
		assertEquals(getY.invoke(st), getY.invoke(getSt.invoke(line2DRef)));
		assertEquals(getX.invoke(en), getX.invoke(getEn.invoke(line2DRef)));
		assertEquals(getY.invoke(en), getY.invoke(getEn.invoke(line2DRef)));
		
		Object st2 = makePoint2D.invoke(null, 2 ,2);
		Object en2 = makePoint2D.invoke(null, 3 ,3);
		
		setSt.invoke(line2DRef, st2);
		setEn.invoke(line2DRef, en2);
		
		assertEquals(getX.invoke(st2), getX.invoke(getSt.invoke(line2DRef)));
		assertEquals(getY.invoke(st2), getY.invoke(getSt.invoke(line2DRef)));
		assertEquals(getX.invoke(en2), getX.invoke(getEn.invoke(line2DRef)));
		assertEquals(getY.invoke(en2), getY.invoke(getEn.invoke(line2DRef)));
		
	}
	
	/*
	 * Test with nested values
	 * 
	 * value Line2D {
	 * 	Point2D st;
	 * 	Point2D en;
	 * }
	 * 
	 */
	@Test(priority=2)
	static public void testCreateLine2D() throws Throwable {
		String fields[] = {"st:LPoint2D;:value", "en:LPoint2D;:value"};
		Class line2DClass = new ValueTypeGenerator().getClass("Line2D", ValueTypeGenerator.generateValue("Line2D", fields));
		Method makeLine2D = line2DClass.getDeclaredMethod("makeValue", new Class[] {point2DClass, point2DClass});
		MethodHandle getSt = generateGetter(line2DClass, "st", point2DClass);
		MethodHandle setSt = generateSetter(line2DClass, "st", point2DClass);
		MethodHandle getEn = generateGetter(line2DClass, "en", point2DClass);
		MethodHandle setEn = generateSetter(line2DClass, "en", point2DClass);
		
		Object st = makePoint2D.invoke(null, 1 ,1);
		Object en = makePoint2D.invoke(null, 4 ,4);
		
		Object line2D = makeLine2D.invoke(null, st, en);
				
		assertEquals(getX.invoke(st), getX.invoke(getSt.invoke(line2D)));
		assertEquals(getY.invoke(st), getY.invoke(getSt.invoke(line2D)));
		assertEquals(getX.invoke(en), getX.invoke(getEn.invoke(line2D)));
		assertEquals(getY.invoke(en), getY.invoke(getEn.invoke(line2D)));
		
		Object st2 = makePoint2D.invoke(null, 2 ,2);
		Object en2 = makePoint2D.invoke(null, 3 ,3);
		
		setSt.invoke(line2D, st2);
		setEn.invoke(line2D, en2);
		
		assertEquals(getX.invoke(st2), getX.invoke(getSt.invoke(line2D)));
		assertEquals(getY.invoke(st2), getY.invoke(getSt.invoke(line2D)));
		assertEquals(getX.invoke(en2), getX.invoke(getEn.invoke(line2D)));
		assertEquals(getY.invoke(en2), getY.invoke(getEn.invoke(line2D)));
		
	}

	/*
	 * Test setting null values
	 */
	/*
	@Test(priority=2)
	static public void testLine2DSetNull() throws Throwable {
		String fields[] = {"st:LPoint2D;:value", "en:LPoint2D;:value"};
		Class line2DClassNull = new ValueTypeGenerator().getClass("Line2DNull", ValueTypeGenerator.generateValue("Line2DNull", fields));
		Method makeLine2DNull = line2DClassNull.getDeclaredMethod("makeValue", new Class[] {point2DClass, point2DClass});
		MethodHandle getSt = generateGetter(line2DClassNull, "st", point2DClass);
		MethodHandle setSt = generateSetter(line2DClassNull, "st", point2DClass);
		MethodHandle getEn = generateGetter(line2DClassNull, "en", point2DClass);
		MethodHandle setEn = generateSetter(line2DClassNull, "en", point2DClass);

		MethodHandle setNullSt = generateNullSetter(line2DClassNull, "st");
		MethodHandle setNullEn = generateNullSetter(line2DClassNull, "en");

		Object st = makePoint2D.invoke(null, 1 ,1);
		Object en = makePoint2D.invoke(null, 4 ,4);

		Object line2D = makeLine2DNull.invoke(null, st, en);

		setNullSt.invoke(line2D);
		setNullEn.invoke(line2D);
	}*/
	
	private static void checkValues(Object o, Object o2) {
		com.ibm.jvm.Dump.SystemDump();		
	}
	
	private static void checkValues(Object o) {
		com.ibm.jvm.Dump.SystemDump();		
	}
	static MethodHandle generateGetter(Class<?> clazz, String fieldName, Class<?> fieldType) {
		try {
			return lookup.findVirtual(clazz, "get"+fieldName, MethodType.methodType(fieldType));
		} catch (IllegalAccessException | SecurityException | NullPointerException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	static MethodHandle generateSetter(Class clazz, String fieldName, Class fieldType) {
		try {
			return lookup.findVirtual(clazz, "set"+fieldName, MethodType.methodType(void.class, fieldType));
		} catch (IllegalAccessException | SecurityException | NullPointerException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	static MethodHandle generateNullSetter(Class clazz, String fieldName) {
		try {
			return lookup.findVirtual(clazz, "setNull"+fieldName, MethodType.methodType(void.class));
		} catch (IllegalAccessException | SecurityException | NullPointerException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	static MethodHandle generateWither(Class clazz, String fieldName, Class fieldType) {
		try {
			return lookup.findVirtual(clazz, "with"+fieldName, MethodType.methodType(clazz, fieldType));
		} catch (IllegalAccessException | SecurityException | NullPointerException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

}
