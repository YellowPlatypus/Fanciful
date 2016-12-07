/*     */ package net.amoebaman.util;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.bukkit.Bukkit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Reflection
/*     */ {
/*     */   private static String _versionString;
/*     */   
/*     */   public static synchronized String getVersion()
/*     */   {
/*  29 */     if (_versionString == null) {
/*  30 */       if (Bukkit.getServer() == null)
/*     */       {
/*  32 */         return null;
/*     */       }
/*  34 */       String name = Bukkit.getServer().getClass().getPackage().getName();
/*  35 */       _versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
/*     */     }
/*     */     
/*  38 */     return _versionString;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  44 */   private static final Map<String, Class<?>> _loadedNMSClasses = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*  48 */   private static final Map<String, Class<?>> _loadedOBCClasses = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized Class<?> getNMSClass(String className)
/*     */   {
/*  57 */     if (_loadedNMSClasses.containsKey(className)) {
/*  58 */       return (Class)_loadedNMSClasses.get(className);
/*     */     }
/*     */     
/*  61 */     String fullName = "net.minecraft.server." + getVersion() + className;
/*  62 */     Class<?> clazz = null;
/*     */     try {
/*  64 */       clazz = Class.forName(fullName);
/*     */     } catch (Exception e) {
/*  66 */       e.printStackTrace();
/*  67 */       _loadedNMSClasses.put(className, null);
/*  68 */       return null;
/*     */     }
/*  70 */     _loadedNMSClasses.put(className, clazz);
/*  71 */     return clazz;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized Class<?> getOBCClass(String className)
/*     */   {
/*  81 */     if (_loadedOBCClasses.containsKey(className)) {
/*  82 */       return (Class)_loadedOBCClasses.get(className);
/*     */     }
/*     */     
/*  85 */     String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
/*  86 */     Class<?> clazz = null;
/*     */     try {
/*  88 */       clazz = Class.forName(fullName);
/*     */     } catch (Exception e) {
/*  90 */       e.printStackTrace();
/*  91 */       _loadedOBCClasses.put(className, null);
/*  92 */       return null;
/*     */     }
/*  94 */     _loadedOBCClasses.put(className, clazz);
/*  95 */     return clazz;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized Object getHandle(Object obj)
/*     */   {
/*     */     try
/*     */     {
/* 108 */       return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
/*     */     } catch (Exception e) {
/* 110 */       e.printStackTrace(); }
/* 111 */     return null;
/*     */   }
/*     */   
/*     */ 
/* 115 */   private static final Map<Class<?>, Map<String, Field>> _loadedFields = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized Field getField(Class<?> clazz, String name)
/*     */   {
/*     */     Map<String, Field> loaded;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 136 */     if (!_loadedFields.containsKey(clazz)) {
/* 137 */        loaded = new HashMap();
/* 138 */       _loadedFields.put(clazz, loaded);
/*     */     } else {
/* 140 */       loaded = (Map)_loadedFields.get(clazz);
/*     */     }
/* 142 */     if (loaded.containsKey(name))
/*     */     {
/* 144 */       return (Field)loaded.get(name);
/*     */     }
/*     */     try {
/* 147 */       Field field = clazz.getDeclaredField(name);
/* 148 */       field.setAccessible(true);
/* 149 */       loaded.put(name, field);
/* 150 */       return field;
/*     */     }
/*     */     catch (Exception e) {
/* 153 */       e.printStackTrace();
/*     */       
/* 155 */       loaded.put(name, null); }
/* 156 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 164 */   private static final Map<Class<?>, Map<String, Map<ArrayWrapper<Class<?>>, Method>>> _loadedMethods = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static synchronized Method getMethod(Class<?> clazz, String name, Class<?>... args)
/*     */   {
/* 188 */     if (!_loadedMethods.containsKey(clazz)) {
/* 189 */       _loadedMethods.put(clazz, new HashMap());
/*     */     }
/*     */     
/* 192 */     Map<String, Map<ArrayWrapper<Class<?>>, Method>> loadedMethodNames = (Map)_loadedMethods.get(clazz);
/* 193 */     if (!loadedMethodNames.containsKey(name)) {
/* 194 */       loadedMethodNames.put(name, new HashMap());
/*     */     }
/*     */     
/* 197 */     Map<ArrayWrapper<Class<?>>, Method> loadedSignatures = (Map)loadedMethodNames.get(name);
/* 198 */     ArrayWrapper<Class<?>> wrappedArg = new ArrayWrapper(args);
/* 199 */     if (loadedSignatures.containsKey(wrappedArg)) {
/* 200 */       return (Method)loadedSignatures.get(wrappedArg);
/*     */     }
/*     */     
/* 203 */     for (Method m : clazz.getMethods())
/* 204 */       if ((m.getName().equals(name)) && (Arrays.equals(args, m.getParameterTypes()))) {
/* 205 */         m.setAccessible(true);
/* 206 */         loadedSignatures.put(wrappedArg, m);
/* 207 */         return m;
/*     */       }
/* 209 */     loadedSignatures.put(wrappedArg, null);
/* 210 */     return null;
/*     */   }
/*     */ }

