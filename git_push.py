import os
import requests
import json
import schedule
import time

last_score = 0
max_score = 0


##
def flush_score():
    global last_score
    global max_score
    data = {"questionId": 2, "pageNum": 1, "size": 10}

    r = requests.post(
        "https://kcode.kuaishou.com/challenge/score/rt/list",
        headers={
            "Content-Type": 'application/json',
            'Origin': 'https://kcode.kuaishou.com'
        },
        data=json.dumps(data),
        cookies={
            "***"
        }
    )
    tmp = json.loads(r.text)['scores'][0]['score']
    try:
        score = float(tmp)
        max_score = max(max_score, score)
    except:
        print(tmp)
    print(max_score)


def git_push():
    with open("./git_push.py", encoding="utf-8", mode="a") as data:
        data.write("# test")
    data.close()

    os.system("git status")
    os.system("git commit -a -m \"flush score\" ")
    os.system("git push")


schedule.every(1).minutes.do(flush_score)
schedule.every(10).minutes.do(git_push)

while True:
    schedule.run_pending()
    time.sleep(1)
