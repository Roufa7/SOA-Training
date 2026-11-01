import urllib.request, urllib.error, json, base64, time, os
from pathlib import Path
base=os.environ.get('SOA_BASE_URL','http://localhost:8080/Gestion_UE_VF-1.0-SNAPSHOT/api').rstrip('/')
password=os.environ['SOA_PASSWORD']
username=os.environ['SOA_USERNAME']
results=[]
def req(label,path,expected,body=None,ctype=None,token=None,method=None):
 headers={}
 if ctype: headers['Content-Type']=ctype
 if token is not None: headers['Authorization']=token
 r=urllib.request.Request(base+path,data=body.encode() if body is not None else None,headers=headers,method=method)
 try: response=urllib.request.urlopen(r)
 except urllib.error.HTTPError as e: response=e
 content=response.read().decode(); status=response.code
 assert status==expected,(label,status,content[:400])
 if status==401: assert response.headers.get('WWW-Authenticate')=='Bearer'
 results.append((label,status,'PASS'))
 return content
req('Missing token','/ue/list',401)
req('Incorrect password','/authentification',403,json.dumps({'username':username,'password':'incorrect'}),'application/json')
req('Missing credentials','/authentification',403,'{}','application/json')
token=req('JSON login','/authentification',200,json.dumps({'username':username,'password':password}),'application/json')
from urllib.parse import urlencode
form=req('Form login','/authentification',200,urlencode({'username':username,'password':password}),'application/x-www-form-urlencoded')

header,payload,sig=token.split('.')
claims=json.loads(base64.urlsafe_b64decode(payload+'=='))
assert claims['exp']-claims['iat']==900 and claims['sub']==username
assert json.loads(base64.urlsafe_b64decode(header+'=='))['alg']=='HS512'
results.append(('JWT HS512 claims and 15-minute lifetime',200,'PASS'))
req('Valid token read','/ue/list',200,token='Bearer '+token)
ue={'code':8101,'domaine':'SOA validation','responsable':'Test','credits':6,'semestre':1}
req('Write without token','/ue/add',401,json.dumps(ue),'application/json')
req('Valid token JSON creation','/ue/add',201,json.dumps(ue),'application/json','Bearer '+form)
xml='<uniteEnseignement><code>8102</code><domaine>SOAP</domaine><responsable>Test</responsable><credits>6</credits><semestre>1</semestre></uniteEnseignement>'
req('Valid token XML creation','/ue/add',201,xml,'application/xml','Bearer '+token)
bad=header+'.'+payload+'.'+('A' if sig[0]!='A' else 'B')+sig[1:]
req('Altered signature','/ue/add',401,json.dumps(ue),'application/json','Bearer '+bad)
req('Malformed token','/ue/add',401,json.dumps(ue),'application/json','Bearer invalid')
req('Wrong authentication scheme','/ue/list',401,token='Basic invalid')
req('Empty bearer','/ue/list',401,token='Bearer ')
req('Case insensitive bearer','/ue/list',200,token='bearer '+token)
req('Public with invalid token','/hello/public',200,token='Bearer invalid')
req('Public without token','/hello/public',200)
ue['domaine']='Updated'
req('Protected update','/ue/update/8101',200,json.dumps(ue),'application/json','Bearer '+token,method='PUT')
req('Protected delete','/ue/delete/8101',200,token='Bearer '+token,method='DELETE')

print('\n'.join(f'{name}: {status} {ok}' for name,status,ok in results))
