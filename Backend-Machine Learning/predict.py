import pandas as pd
import numpy as np
from sklearn import linear_model
import MySQLdb
import socket
import sys
import json

def read_data(sql):
    # Open database connection
    db = MySQLdb.connect(host="localhost",user="root",passwd="",db="college_db",port=3306 )
    cursor = db.cursor()
    try:
        cursor.execute(sql)
        data = cursor.fetchall()
        db.close()
        return data
    except Exception as e:
        print e
        
def predict_adm(location,college,features):
    result = dict()
    branches = ['CS','ECE','MECH']

    for branch in branches:    
        if college == 0:
            query="select * from admission_data where branch_name='"+branch+"' and location='"+location+"' order by year"
        else:
            query="select * from admission_data where branch_name='"+branch+"' and college_name='"+college+"' order by year"
        data=read_data(query)
        
        df = pd.DataFrame(list(data), columns=['college_name','year','rank','branch_name','placement_percent','nba_accr','location','cut_off','admission'])
        X = df.iloc[:,[2,4,5,7]].values
        a = np.array(features)
        X = np.vstack((X,a))

        Y = df.iloc[:,len(df.columns)-1].values
        
        from sklearn.preprocessing import LabelEncoder,OneHotEncoder
        labelencoder_X = LabelEncoder()
        
        X[:,2]=labelencoder_X.fit_transform(X[:,2])
        onehotencoder = OneHotEncoder(categorical_features = [2])
        X=onehotencoder.fit_transform(X).toarray()
        
        X = np.delete(X,np.s_[-1:],0)
        X_Test = np.delete(X,np.s_[:-1],0)

        ols = linear_model.LinearRegression()
        ols.fit(X,Y)
        print ['Rank','Branch Placement', 'NBA Accreditation', 'Branch Cut-Off']
        print ols.coef_
        result[branch] = round(ols.predict(X_Test)[0],2);
    return result

def predict_plcmnt(location,college,features):
    result = dict()
    branches = ['CS','ECE','MECH']

    for branch in branches:    
        if college == 0:
            query="select * from placement where branch_name='"+branch+"' and location='"+location+"' order by year"
        else:
            query="select * from placement where branch_name='"+branch+"' and college_name='"+college+"' order by year"
        data=read_data(query)
        
        df = pd.DataFrame(list(data))
        X = df.iloc[:,[3,4,6,7,8]].values
        
        if branch == 'CS':
            features.insert(2,(df.iloc[-1:,6].values)[0])
            features.insert(5,(df.iloc[-1:,8].values)[0])

        a = np.array(features)
        X = np.vstack((X,a))

        Y = df.iloc[:,len(df.columns)-1].values        
        X = np.delete(X,np.s_[-1:],0)
        X_Test = np.delete(X,np.s_[:-1],0)

        ols = linear_model.LinearRegression()
        ols.fit(X,Y)
        print ['Number of Companies','Number of Partner Companies', 'Quality of Trainig', 'Average Salary Package', 'Industry Feedback']
        print ols.coef_
        result[branch] = round(ols.predict(X_Test)[0],2);
    return result


def read_data_user():
    HOST = '192.168.43.89' #this is your localhost
    PORT = 8881
    
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    print 'socket created'

    #Bind socket to Host and Port
    try:
        s.bind((HOST, PORT))
    except socket.error as err:
        print 'Bind Failed, Error Code: ' + str(err[0]) + ', Message: ' + err[1]
        sys.exit()
    print 'Socket Bind Success!'

    #listen(): This method sets up and start TCP listener.
    s.listen(10)
    print 'Socket is now listening'

    while 1:
        conn, addr = s.accept()
        print 'Connect with ' + addr[0] + ':' + str(addr[1])
        buf = conn.recv(1024)
        buf = buf.strip()
        d = json.loads(buf.strip());
        
        location = dict()
        adm = list();
        admj = d[0]['admission']
        adm.append(admj['rank'])
        adm.append(admj['branch_prev_yr_placement'])
        adm.append(admj['nba_accreditation'])
        adm.append(admj['branch_cut_off'])
        location['admission'] = predict_adm(admj['location'],0,adm)
        
        plcmnt = list()
        plcmntj = d[1]['placement']
        plcmnt.append(plcmntj['number_of_companies_visited'])
        plcmnt.append(plcmntj['avg_salary_package'])
        plcmnt.append(plcmntj['number_of_partner_companies'])
        location['placement'] = predict_plcmnt(admj['location'],0,plcmnt)  

        print '\n',        
        
        college = dict()        
        college['admission'] = predict_adm(0,admj['college_name'],adm)
        
        plcmnt = list()
        plcmntj = d[1]['placement']
        plcmnt.append(plcmntj['number_of_companies_visited'])
        plcmnt.append(plcmntj['number_of_partner_companies'])
        plcmnt.append(plcmntj['avg_salary_package'])
        college['placement'] = predict_plcmnt(0,admj['college_name'],plcmnt)
        
        data0 = dict()
        data0['location'] = location
        data1 = dict()        
        data1['college'] = college
        
        arr = list()
        arr.append(data0)
        arr.append(data1)
        #score=
        conn.send(json.dumps(arr, ensure_ascii=False));
        conn.send('\n');
    conn.close();
    s.close()
read_data_user()