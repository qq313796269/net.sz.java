using UnityEngine;
using System.Collections;
using UnityEditor;
using System.Collections.Generic;
using System.IO;
using System.Text;
using UnityEditor.SceneManagement;

public class NavMeshWindow : EditorWindow
{
    private float endX = 128;
    private float endZ = 128;
    private float agentRadius = 0.5f;
    private float startX = 0f;
    private float startZ = 0f;
    private int mapID;
    private GameObject map;
    private GameObject xingzouceng;
    private GameObject feixingceng;
    private GameObject qinggong;
    private MapProperty property;
    [MenuItem("Build/生成寻路数据")]
    static void ShowSkillDebugWindow()
    {
        Rect wr = new Rect(0, 0, 500, 400);
        NavMeshWindow window = (NavMeshWindow)EditorWindow.GetWindowWithRect(typeof(NavMeshWindow), wr, true, "寻路数据生成");
        window.Show();
    }

    public void Show()
    {
        base.Show();
        init();
    }

    private void init()
    {
        ClearTmp();
        map = GameObject.Find(EditorSceneManager.GetActiveScene().name);
        if (map == null)
        {
            Debug.LogError("地图名和地图中场景节点名不一致或已隐藏地图节点，请打开");
            Close();
        }
        property = map.GetComponent<MapProperty>();
        if (property==null)
        {
            property=map.AddComponent<MapProperty>();
        }
        this.endX = property.endX;
        this.endZ = property.endZ;
        this.agentRadius = property.agentRadius;
        this.startX = property.startX;
        this.startZ = property.startZ;
        try
        {
            mapID = int.Parse(Path.GetFileNameWithoutExtension(EditorSceneManager.GetActiveScene().name).Replace("map", ""));
        }
        catch (System.Exception)
        {
            Debug.LogError("地图id转换错误，请确保场景节点名字为：map数字 格式");
            Close();
        }
        xingzouceng = GameObject.Find("xingzouceng");
        feixingceng = GameObject.Find("feixingceng");
        qinggong = GameObject.Find("zudang");
        if (xingzouceng == null)
        {
            Debug.LogError("行走层找不到，请确保名字为xingzouceng");
            Close();
            return;
        }
        if (feixingceng == null)
        {
            Debug.LogError("飞行层找不到，请确保名字为feixingceng");
        }
        if (qinggong == null)
        {
            Debug.LogError("阻挡层找不到，请确保名字为zudang");
        }
    }

    private void OnDestroy()
    {
        mapID = 0;
        map = null;
    }

    void OnGUI()
    {
        EditorGUILayout.LabelField("注意事项1：请确保地图节点为map数字形式");
        EditorGUILayout.LabelField("注意事项2：行走层命名为xingzouceng,飞行层命名为feixingceng，阻挡层命名为qinggong");
        EditorGUILayout.Separator();
        EditorGUILayout.LabelField("当前地图id为：" + mapID);
        EditorGUILayout.Separator();
        EditorGUILayout.LabelField("请输入地图参数");
        endX = EditorGUILayout.FloatField("地图截止X坐标", endX);
        endZ = EditorGUILayout.FloatField("地图截止Z坐标", endZ);
        startX = EditorGUILayout.FloatField("地图起始X坐标", startX);
        startZ = EditorGUILayout.FloatField("地图起始Z坐标", startZ);
        EditorGUILayout.FloatField("地图宽度", endX- startX);
        EditorGUILayout.FloatField("地图高度", endZ - startZ);
		agentRadius = EditorGUILayout.FloatField ("网格的参数:",agentRadius);
        if (GUILayout.Button("测试地图大小"))
        {
            CreateMapTestMesh();
        }
        EditorGUILayout.Separator();
        if (GUILayout.Button("生成CS寻路数据"))
        {
            if (EditorSceneManager.GetActiveScene().name.Contains(mapID.ToString()))
            {
                CreateFlatMesh();
            }
        }

        EditorGUILayout.Separator();

        //EditorGUILayout.LabelField("打地图寻路层");
        //agentRadius = EditorGUILayout.FloatField("Agent Radius", agentRadius);
        if (GUILayout.Button("重载配置"))
        {
            init();
        }
    }

    void CreateMapTestMesh()
    {
        map.SetActive(true);
        GameObject UnWalkAble = createOb(null, "MapTest", 0);
        Mesh UnWalkMesh = UnWalkAble.GetComponent<MeshFilter>().sharedMesh;
        UnWalkMesh.vertices = new Vector3[] {
            new Vector3(startX, 0, startZ),
            new Vector3(startX, 0, endZ+startZ),
            new Vector3(endX+startX, 0, endZ+startZ),
            new Vector3(endX+startX, 0, startZ)
        };
        UnWalkMesh.triangles = new int[] { 0, 1, 2, 0, 2, 3 };
    }

    void BuildFloorNavMesh(float agentRadius)
    {
        map.SetActive(false);
        SetAgentRadius(agentRadius);
        if (feixingceng != null)
            feixingceng.GetComponent<Renderer>().enabled = false;
        if (qinggong != null)
            qinggong.GetComponent<Renderer>().enabled = false;
        xingzouceng.GetComponent<Renderer>().enabled = true;
        NavMeshBuilder.ClearAllNavMeshes();
        NavMeshBuilder.BuildNavMesh();
        xingzouceng.GetComponent<Renderer>().enabled = false;
        map.SetActive(true);
    }

    void BuildAllNavMesh(float agentRadius)
    {
        map.SetActive(false);
        ClearTmp();
        SetAgentRadius(agentRadius);
        xingzouceng.GetComponent<Renderer>().enabled = true;
        if (feixingceng != null)
            feixingceng.GetComponent<Renderer>().enabled = true;
        if (qinggong != null)
            qinggong.GetComponent<Renderer>().enabled = true;
        NavMeshBuilder.ClearAllNavMeshes();
        NavMeshBuilder.BuildNavMesh();
        xingzouceng.GetComponent<Renderer>().enabled = false;
        if (feixingceng != null)
            feixingceng.GetComponent<Renderer>().enabled = false;
        if (qinggong != null)
            qinggong.GetComponent<Renderer>().enabled = false;
        map.SetActive(true);
    }

    void ClearTmp()
    {
        GameObject MapTest = GameObject.Find("MapTest");
        if (MapTest)
        {
            Object.DestroyImmediate(MapTest);
        }
        GameObject NavMesh_WalkAble = GameObject.Find("NavMesh_WalkAble");
        if (NavMesh_WalkAble)
        {
            Object.DestroyImmediate(NavMesh_WalkAble);
        }
        GameObject NavMesh_UnWalkAble = GameObject.Find("NavMesh_UnWalkAble");
        if (NavMesh_UnWalkAble)
        {
            Object.DestroyImmediate(NavMesh_UnWalkAble);
        }
    }

    void BuildNavMesh(float agentRadius)
    {
        SetAgentRadius(agentRadius);
        NavMeshBuilder.ClearAllNavMeshes();
        NavMeshBuilder.BuildNavMesh();
    }

    //创建平面的面片
    void CreateFlatMesh()
    {
        lock (this)
        {
            BuildAllNavMesh(agentRadius);
            BuildFloorNavMesh(agentRadius);
            map.SetActive(false);
            xingzouceng.GetComponent<Renderer>().enabled = false;
            if (feixingceng != null)
                feixingceng.GetComponent<Renderer>().enabled = false;
            if (qinggong != null)
                qinggong.GetComponent<Renderer>().enabled = false;
            NavMeshTriangulation triangulatedNavMesh = NavMesh.CalculateTriangulation();
            GameObject WalkAble = createOb(xingzouceng, "NavMesh_WalkAble", 1);
            Vector3[] pathVertices = triangulatedNavMesh.vertices;
            int[] triangles = triangulatedNavMesh.indices;
            Mesh WalkMesh = WalkAble.GetComponent<MeshFilter>().sharedMesh;
            Vector3[] vertexes = new Vector3[pathVertices.Length];
            for (int i = 0; i < pathVertices.Length; i++)
            {
                float x = pathVertices[i].x;
                float z = pathVertices[i].z;
                vertexes[i] = new Vector3(x, 0, z);
            }            
            WalkMesh.vertices = vertexes;
            WalkMesh.triangles = triangles;

            GameObject UnWalkAble = createOb(xingzouceng, "NavMesh_UnWalkAble", 0);
            Mesh UnWalkMesh = UnWalkAble.GetComponent<MeshFilter>().sharedMesh;
            UnWalkMesh.vertices = new Vector3[] {
                new Vector3(startX, 0, startZ),
                new Vector3(startX, 0, endZ),
                new Vector3(endX, 0, endZ),
                new Vector3(endX, 0, startZ)
            };
            UnWalkMesh.triangles = new int[] { 0, 1, 2, 0, 2, 3 };
			SetAgentRadius(agentRadius);
            NavMeshBuilder.ClearAllNavMeshes();
            NavMeshBuilder.BuildNavMesh();
            Object.DestroyImmediate(WalkAble);
            Object.DestroyImmediate(UnWalkAble);
            string path = System.Environment.CurrentDirectory.Replace("\\", "/") + "/Nav_build/";
            if (!Directory.Exists(path))
            {
                Directory.CreateDirectory(path);
            }
            StringBuilder sb = new StringBuilder("{");
            sb.Append("\"mapID\":").Append(mapID);
            sb.Append(",\"startX\":").Append(startX).Append(",\"startZ\":").Append(startZ);
            sb.Append(",\"endX\":").Append(endX).Append(",\"endZ\":").Append(endZ);
            string filename = path + mapID + ".navmesh";
            triangulatedNavMesh = NavMesh.CalculateTriangulation();
            string data = MeshToString(triangulatedNavMesh, true);
            if (data.Length < 128)
            {
                alert("阻挡未打入！");
                return;
            }
            sb.Append(",").Append(data);
            BuildFloorNavMesh(agentRadius);
            triangulatedNavMesh = NavMesh.CalculateTriangulation();
            data = MeshToString(triangulatedNavMesh, false);
            if (data.Length < 128)
            {
                alert("寻路未打入！");
                return;
            }
            sb.Append(",").Append(data).Append("}");
            MeshToFile(filename, sb.ToString());
            BuildAllNavMesh(agentRadius);
            map.SetActive(true);
            alert("成功！");

            property.endX=this.endX;
            property.endZ=this.endZ;
            property.agentRadius=this.agentRadius;
            property.startX=this.startX;
            property.startZ=this.startZ;
            EditorSceneManager.SaveOpenScenes();
        }
        //Close();
    }

    private void alert(string content)
    {
        this.ShowNotification(new GUIContent(content));
    }

    private GameObject createOb(GameObject source, string name, int WalkLayer)
    {
        GameObject ob = GameObject.Find(name);
        Mesh walkMesh = new Mesh();
        walkMesh.name = name;
        if (ob == null)
        {
            ob = new GameObject(name);
            ob.AddComponent<MeshFilter>();//网格
            ob.AddComponent<MeshRenderer>();//网格渲染器  
        }
        if (source)
        {
            //ob.transform.localRotation = source.transform.localRotation;
            //ob.transform.position = source.transform.position;
            //ob.transform.SetParent(source.transform.parent);
        }
        ob.GetComponent<MeshFilter>().sharedMesh = walkMesh;
        GameObjectUtility.SetStaticEditorFlags(ob, StaticEditorFlags.NavigationStatic);
        GameObjectUtility.SetNavMeshArea(ob, WalkLayer);
        return ob;
    }

    private void SetAgentRadius(float agentRadius)
    {
        SerializedObject settingsObject = new SerializedObject(NavMeshBuilder.navMeshSettingsObject);
        SerializedProperty agentRadiusSettings = settingsObject.FindProperty("m_BuildSettings.agentRadius");

        agentRadiusSettings.floatValue = agentRadius;

        settingsObject.ApplyModifiedProperties();
    }

    static string MeshToString(NavMeshTriangulation mesh, bool isBlock)
    {
        StringBuilder sb = new StringBuilder();
        sb.Append(isBlock ? "\"blockTriangles\":[" : "\"pathTriangles\":[");
        for (int i = 0; i < mesh.indices.Length; i++)
        {
            sb.Append(mesh.indices[i]).Append(",");
        }
        sb.Length--;
        sb.Append("],");

        sb.Append(isBlock ? "\"blockVertices\":[" : "\"pathVertices\":[");
        for (int i = 0; i < mesh.vertices.Length; i++)
        {
            Vector3 v = mesh.vertices[i];
            if (!isBlock && v.y < 1)
            {
                Debug.LogWarning("寻路mesh坐标小于1" + v.y);
            }
            sb.Append("{\"x\":").Append(v.x).Append(",\"y\":").Append(isBlock ? 0 : v.y).Append(",\"z\":").Append(v.z).Append("},");
        }
        sb.Length--;
        sb.Append("]");
        return sb.ToString();
    }

    static void MeshToFile(string filename, string meshData)
    {
        using (StreamWriter sw = new StreamWriter(filename))
        {
            sw.Write(meshData);
        }
    }
}
