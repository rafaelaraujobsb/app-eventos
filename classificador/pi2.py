import sys
from app import app

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=sys.argv[1], debug=eval(sys.argv[2]))
