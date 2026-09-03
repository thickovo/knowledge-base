import requests
import pytest
import time

BASE_URL = "http://localhost:8080/api"


def get_token():
    """获取登录token，供其他测试用例复用"""
    url = f"{BASE_URL}/user/login"
    resp = requests.post(url, json={
        "username": "admin",
        "password": "123456"
    })
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    return data.get("data")


def create_document(token, title="测试文档", content="测试内容", parent_id=0, tag_ids=None):
    """创建文档，返回文档ID"""
    if tag_ids is None:
        tag_ids = []
    url = f"{BASE_URL}/document/create"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {"title": title, "content": content, "parentId": parent_id, "tagIds": tag_ids}
    resp = requests.post(url, json=payload, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    # 从返回的 data 中提取文档 ID，这里假设创建成功后返回了文档ID
    # 如果返回的是 "创建成功" 字符串，则需要改成从列表查询获取
    return data.get("data")


def test_login_success():
    """正常登录，返回token"""
    token = get_token()
    assert token is not None
    print("✅ 登录成功")


def test_login_wrong_password():
    """密码错误，返回错误码"""
    url = f"{BASE_URL}/user/login"
    resp = requests.post(url, json={
        "username": "admin",
        "password": "wrongpassword"
    })
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") != 200
    print("✅ 密码错误测试通过")


def test_login_user_not_exist():
    """用户名不存在，返回错误码"""
    url = f"{BASE_URL}/user/login"
    resp = requests.post(url, json={
        "username": "notexist",
        "password": "123456"
    })
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") != 200
    print("✅ 用户名不存在测试通过")


def test_login_empty_username():
    """用户名为空，返回错误码"""
    url = f"{BASE_URL}/user/login"
    resp = requests.post(url, json={
        "username": "",
        "password": "123456"
    })
    assert resp.status_code == 200
    data = resp.json()
    # 参数校验会返回 code=0 或 code!=200
    assert data.get("code") != 200
    print("✅ 空用户名测试通过")



def test_document_list():
    """查询文档列表"""
    token = get_token()
    url = f"{BASE_URL}/document/list"
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.get(url, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    assert isinstance(data.get("data"), list)
    print(f"✅ 查询文档列表成功，共 {len(data.get('data'))} 篇文档")


def test_document_create():
    """创建文档"""
    token = get_token()
    url = f"{BASE_URL}/document/create"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {
        "title": "Python自动化测试文档",
        "content": "这是由Python脚本自动创建的测试文档",
        "parentId": 0,
        "tagIds": []
    }
    resp = requests.post(url, json=payload, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 创建文档成功：{payload['title']}")
    return data.get("data")


def test_document_create_missing_title():
    """创建文档时缺少标题，应返回参数校验错误"""
    token = get_token()
    url = f"{BASE_URL}/document/create"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {
        "content": "缺少标题的文档",
        "parentId": 0,
        "tagIds": []
    }
    resp = requests.post(url, json=payload, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    # 参数校验失败，code 应为 0
    assert data.get("code") == 0
    print(f"✅ 缺少标题测试通过：{data.get('message')}")


def test_document_update():
    """更新文档"""
    token = get_token()
    # 先创建一个文档
    create_resp = requests.post(
        f"{BASE_URL}/document/create",
        json={"title": "待更新文档", "content": "原始内容", "parentId": 0, "tagIds": []},
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    )
    assert create_resp.status_code == 200
    doc_id = create_resp.json().get("data")
    # 如果返回的是"创建成功"字符串，需要从列表获取ID
    if isinstance(doc_id, str) and "成功" in doc_id:
        # 查询列表获取最新文档ID
        list_resp = requests.get(
            f"{BASE_URL}/document/list",
            headers={"Authorization": f"Bearer {token}"}
        )
        docs = list_resp.json().get("data", [])
        if docs:
            doc_id = docs[-1].get("id")
        else:
            print("⚠️ 无法获取文档ID，跳过更新测试")
            return

    # 更新文档
    url = f"{BASE_URL}/document/update"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {
        "id": doc_id,
        "title": "更新后的标题",
        "content": "更新后的内容"
    }
    resp = requests.put(url, json=payload, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 更新文档成功：ID={doc_id}")


def test_document_delete():
    """删除文档"""
    token = get_token()
    # 先创建一个文档
    create_resp = requests.post(
        f"{BASE_URL}/document/create",
        json={"title": "待删除文档", "content": "将被删除", "parentId": 0, "tagIds": []},
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    )
    assert create_resp.status_code == 200
    doc_id = create_resp.json().get("data")
    if isinstance(doc_id, str) and "成功" in doc_id:
        list_resp = requests.get(
            f"{BASE_URL}/document/list",
            headers={"Authorization": f"Bearer {token}"}
        )
        docs = list_resp.json().get("data", [])
        if docs:
            doc_id = docs[-1].get("id")
        else:
            print("⚠️ 无法获取文档ID，跳过删除测试")
            return

    # 删除文档
    url = f"{BASE_URL}/document/delete/{doc_id}"
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.delete(url, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 删除文档成功：ID={doc_id}")


def test_document_search():
    """搜索文档（关键词）"""
    token = get_token()
    url = f"{BASE_URL}/document/list"
    headers = {"Authorization": f"Bearer {token}"}
    params = {"keyword": "Python"}
    resp = requests.get(url, headers=headers, params=params)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 搜索关键词'Python'，找到 {len(data.get('data', []))} 篇文档")



def test_tag_create():
    """创建标签"""
    token = get_token()
    url = f"{BASE_URL}/tag/create"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {"name": f"Python自动化标签_{int(time.time())}"}
    resp = requests.post(url, json=payload, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 创建标签成功：{payload['name']}")


def test_tag_list():
    """查询标签列表"""
    token = get_token()
    url = f"{BASE_URL}/tag/list"
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.get(url, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    assert isinstance(data.get("data"), list)
    print(f"✅ 查询标签列表成功，共 {len(data.get('data', []))} 个标签")


def test_tag_update():
    """更新标签"""
    token = get_token()
    # 先创建一个标签
    create_resp = requests.post(
        f"{BASE_URL}/tag/create",
        json={"name": "待更新标签"},
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    )
    assert create_resp.status_code == 200
    tag_id = create_resp.json().get("data")
    if isinstance(tag_id, str) and "成功" in tag_id:
        list_resp = requests.get(
            f"{BASE_URL}/tag/list",
            headers={"Authorization": f"Bearer {token}"}
        )
        tags = list_resp.json().get("data", [])
        if tags:
            tag_id = tags[-1].get("id")
        else:
            print("⚠️ 无法获取标签ID，跳过更新测试")
            return

    url = f"{BASE_URL}/tag/update"
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    payload = {"id": tag_id, "name": "更新后的标签名"}
    resp = requests.put(url, json=payload, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 更新标签成功：ID={tag_id}")


def test_tag_delete():
    """删除标签"""
    token = get_token()
    # 先创建一个标签
    create_resp = requests.post(
        f"{BASE_URL}/tag/create",
        json={"name": "待删除标签"},
        headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json"}
    )
    assert create_resp.status_code == 200
    tag_id = create_resp.json().get("data")
    if isinstance(tag_id, str) and "成功" in tag_id:
        list_resp = requests.get(
            f"{BASE_URL}/tag/list",
            headers={"Authorization": f"Bearer {token}"}
        )
        tags = list_resp.json().get("data", [])
        if tags:
            tag_id = tags[-1].get("id")
        else:
            print("⚠️ 无法获取标签ID，跳过删除测试")
            return

    url = f"{BASE_URL}/tag/delete/{tag_id}"
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.delete(url, headers=headers)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ 删除标签成功：ID={tag_id}")


def test_ai_chat():
    """AI对话接口"""
    token = get_token()
    url = f"{BASE_URL}/ai"
    headers = {"Authorization": f"Bearer {token}"}
    params = {"message": "查询我的文档"}
    resp = requests.get(url, headers=headers, params=params)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") == 200
    print(f"✅ AI对话成功，返回数据长度：{len(data.get('data', ''))}")


def test_ai_chat_empty_message():
    """AI对话空消息"""
    token = get_token()
    url = f"{BASE_URL}/ai"
    headers = {"Authorization": f"Bearer {token}"}
    params = {"message": ""}
    resp = requests.get(url, headers=headers, params=params)
    assert resp.status_code == 200
    data = resp.json()
    assert data.get("code") != 200
    print(f"✅ 空消息测试通过：{data.get('message')}")


def test_unauthorized_access():
    """未登录访问文档列表，应返回 401"""
    url = f"{BASE_URL}/document/list"
    resp = requests.get(url)
    assert resp.status_code == 401
    print("✅ 未登录访问测试通过")


def test_invalid_token():
    """无效 Token 访问，应返回 401"""
    url = f"{BASE_URL}/document/list"
    headers = {"Authorization": "Bearer invalid_token"}
    resp = requests.get(url, headers=headers)
    assert resp.status_code == 401
    print("✅ 无效Token测试通过")